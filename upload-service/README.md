# Upload-service 

This project is used to present Quarkus in several conferences.

Hibernate ORM with Panache quickstart - Quarkus</h1>
This application demonstrates how a Quarkus application implements a CRUD endpoint to manage pictures using Hibernate ORM with Panache and Quarkus Spring Web extension 
This management interface invokes the CRUD service endpoint, which interacts with a database using JPA and several other well known libraries.

Behind the scenes, we have:
- Hibernate ORM with Panache taking care of all CRUD operations
- Quarkus Spring Web top of RESTEasy powering the REST API
- ArC, a CDI based dependency injection framework
- the Narayana Transaction Manager coordinating all transactions
- Agroal, the high performance Datasource implementation
- The Undertow webserver

To add a new picture you can curl the endpoint:

```bash
curl --header "Content-Type: application/json" \                                                                                                                               ✔  16:23:59 
--request POST \
--data '{"title":"my-selfie" }' \
http://localhost:8080/pictures
```

Run a postgresql data base using docker:

````bash
docker run --ulimit memlock=-1:-1 -it --rm=true --memory-swappiness=0 --name quarkus_test -e POSTGRES_USER=quarkus_test -e POSTGRES_PASSWORD=quarkus_test -e POSTGRES_DB=quarkus_test -p 5432:5432 postgres:14.5
````

You can copy a few images in the container and adjust the `import.sql` file accordingly:

```
docker cp /Users/auri/pictures/myselfie.jpg ef7a6fdcbb9f:var/lib/postgresql/data/
```

**Note**: ef7a6fdcbb9f is the container ID to which the myselfie.jpg will be copied. 

Then, initialize the database with it adding the following line in ìmport.sql:

```sql
INSERT INTO picture(title, image) VALUES ('My selfie', pg_read_binary_file('/var/lib/postgresql/data/myselfie.jpg'));
```

Demo Time

### Bootstrapping the project

- Go to https://code.quarkus.io/ and select spring-data-jpa, spring-web extension, quarkus-jdbc-postgresql
- Download 
- Unzip
- Go to the root directory
- Open IDEA

### Crate a Pictures microservice accessing a Postgresql database

- Update Quarkus version to 3.0.0.CR2
- Launch dev mode: `quarkus dev`
- Open a browser to: localhost:8080/greeting
- Add Picture entity
```java
package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.Date;

@Entity
public class Picture extends PanacheEntity {

    public String title;

    public String owner;

    @CreationTimestamp
    public Date created;


    @Lob
    @Column(columnDefinition = "BYTEA")
    @JdbcTypeCode(Types.BINARY)
    public byte[] image;

    public Picture() {
    }
}
````
Add both constructors? or only one?

- Add a `PictureController.java` like this:
````java
package org.acme;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pictures")
public class PictureController {

    @GetMapping
    public List<Picture> findAll(){
        return Picture.findAll().list();

    }
}
````
- Add following database related properties:
````properties
quarkus.datasource.db-kind=postgresql
quarkus.hibernate-orm.database.generation=drop-and-create
````
- Open browser: http://localhost:8080/pictures

- Add new method for adding pictures and a record class to wrap the picture fields:
````java
package org.acme;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pictures")
public class PictureController {

    public record PictureRequest(String title, String owner, byte[] image){};

    @GetMapping
    public List<Picture> findAll(){
        return Picture.findAll().list();

    }

    @PostMapping("/new")
    @Transactional
    public ResponseEntity<Picture> add (PictureRequest request){
        Picture picture = new Picture(request.title, request.owner, request.image);
        Picture.persist(picture);
        return new ResponseEntity<>(picture, HttpStatus.CREATED);
    }
}
````

Check the new endpoint by curl:
````bash
curl --header "Content-Type: application/json" --request POST --data '{"title":"my-selfie" }' http://localhost:8080/pictures/new
````

### Add a better UI

In order to put in place a better front end and UI, add the following dependency to the `pom.xml` file:

```xml
<dependency>
      <groupId>org.instaquarkm</groupId>
      <artifactId>frontend</artifactId>
      <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### Add security layer with WebAuthn

We will add a security layer with a new dependency ready to use that provides authentification and registering using the Quarkus WebAuthn extension.

```xml
<dependency>
    <groupId>org.instaquarkm</groupId>
    <artifactId>webauthn-authentication</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### Using the Rest Client

We are using the Rest Client in order to interact with a remote API. In this case, it is a function running on AWS Lambda.

#### Add the Rest Client dependencies:
```xml
<dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-rest-client-reactive</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-rest-client-reactive-jackson</artifactId>
    </dependency>
```
#### Create the interface. 
Using the RESTEasy REST Client is as simple as creating an interface using the proper JAX-RS and MicroProfile annotations. In our case the interface should be created

```java
package org.instaquarm.uploading.client;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.instaquarm.uploading.Picture;


@RegisterRestClient(configKey = "squarer-function")
public interface SquarerRestClient {

    @POST
    @Path("/squarer")
    Picture makeItSquare(Picture squarePicture);
}
```
- @RegisterRestClient allows Quarkus to know that this interface is meant to be available for CDI injection as a REST Client
- @Path and @POST are the standard JAX-RS annotations used to define how to access the service.
- `@RegisterRestClient configKey` facilitates the configuration because allows to use another configuration root than the fully qualified name of your interface.

#### Create the config

To determine the base URL to which REST calls will be made, the REST Client uses configuration from application.properties.

````properties
quarkus.rest-client.squarer-function.url=https://vaekn02h31.execute-api.us-east-1.amazonaws.com/stage/squarer
````

#### Create the actual client

Let's create a class, CDI bean, that will wrap the calls to the function.

```java
package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class SquarerClient {

    @RestClient
    SquarerRestClient squarerRestClient;

    public Picture makeItSquare(Picture picture){
        return squarerRestClient.makeItSquarer(picture);
    }

}
```
Note that the `SquarerRestClient` is injected using the `RestClient` annotation.

And finally, add the call to the function in the `PictureController`: 

````java
package org.acme;


import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pictures")
public class PictureController {

    @Inject
    SquarerClient squarerClient;

    public record PictureRequest(String title, String owner, byte[] image){};

    @GetMapping
    public List<Picture> findAll(){
        return Picture.findAll().list();

    }

    @PostMapping("/new")
    @Transactional
    public ResponseEntity<Picture> add (PictureRequest request){
        Picture picture = new Picture(request.title, request.owner, request.image);
        Picture.persist(picture);
        squarerClient.makeItSquare(picture);
        return new ResponseEntity<>(picture, HttpStatus.CREATED);
    }
}

````

### Fault Tolerance

### Health check

### Metrics

### Prod configuration

### Deploy to OpenShift
