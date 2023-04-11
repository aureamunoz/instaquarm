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
      <version>1.0.0</version>
</dependency>
```

### Add security layer with WebAuthn

We will add a security layer with a new dependency ready to use that provides authentification and registering using the Quarkus WebAuthn extension.

```xml
<dependency>
    <groupId>org.instaquarkm</groupId>
    <artifactId>webauthn-authentication</artifactId>
    <version>1.0.0</version>
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
quarkus.rest-client.squarer-function.url=https://vaekn02h31.execute-api.us-east-1.amazonaws.com/stage
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
Our endpoint requires communication to an external service, the Squarer function, which introduces a factor of unreliability.
We will use SmallRye Fault Tolerance extension to simplify making more resilient uploading-service.


#### Add the extension dependency

This time we will use the following command instead of editing the pom.xml file:

```bash
quarkus extension add 'smallrye-fault-tolerance'
```

Before move forward, we are going to introduce a ``maybeFail`` method in our code that will cause failures in about the 50% of requests.
So, add the following code to the `SquarerClient` class:

````java
package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Random;

@ApplicationScoped
public class SquarerClient {

    @RestClient
    SquarerRestClient squarerRestClient;

    public Picture makeItSquare(Picture picture){
        maybeFail();
        return squarerRestClient.makeItSquarer(picture);
    }

    private void maybeFail() {
        if (new Random().nextBoolean()) {
            throw new RuntimeException("Resource failure.");
        }
    }

}

````

We can test this and see how some requests fail.

#### Adding Resiliency: Retries

Add the @Retry annotation to the `SquarerClient#makeItSquare()` method as follows:

````java
package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Random;

@ApplicationScoped
public class SquarerClient {

    @RestClient
    SquarerRestClient squarerRestClient;

    @Retry
    public Picture makeItSquare(Picture picture) {
        maybeFail();
        return squarerRestClient.makeItSquarer(picture);
    }

    private void maybeFail() {
        if (new Random().nextBoolean()) {
            throw new RuntimeException("Resource failure.");
        }
    }

}
````
Warning, in order to not break your system, the method annotated with `@Retry` must be idempotent.
Now, practically all requests should be succeeding. The `SquarerClient#makeItSquare()` method is still in fact failing in about 50 % of time, but every time it happens, the platform will automatically retry the call!
The max retries by default are 3, but you can configure more if you wand using the 'maxRetries' configuration in the annotation.
To see that the failures still happen, check the output of the development server.

#### Adding Resiliency: Timeouts

So what else have we got in MicroProfile Fault Tolerance? Let’s look into timeouts.
As we have seen, the Squarer function takes a few seconds to execute. This is not a critical functionality but a nice-to-have, so we could want rather time out and return the response if the time is greater than some thumbnail. This can be done bya the `Timeout` annotation.

Add the ``Timeout`` annotation and configure 7 seconds to be sure that an exception will be thrown.
````java
package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Random;

@ApplicationScoped
public class SquarerClient {

    @RestClient
    SquarerRestClient squarerRestClient;

    @Retry
    @Timeout(value = 7, unit = ChronoUnit.SECONDS)
    public Picture makeItSquare(Picture picture) {
        //maybeFail();
        return squarerRestClient.makeItSquarer(picture);
    }

    private void maybeFail() {
        if (new Random().nextBoolean()) {
            throw new RuntimeException("Resource failure.");
        }
    }

}
````
Note that the `maybeFail()` call has been commented out in order to demonstrate that a timeout is triggered.

#### Adding Resiliency: Circuit Breaker

A circuit breaker is useful for limiting number of failures happening in the system, when part of the system becomes temporarily unstable. 
The circuit breaker records successful and failed invocations of a method, and when the ratio of failed invocations reaches the specified threshold, the circuit breaker opens and blocks all further invocations of that method for a given time.

Let's add a `CircuitBreaker` annotation to our `makeItSquarer` method and test it with default values.

````java
package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Random;

@ApplicationScoped
public class SquarerClient {

    @RestClient
    SquarerRestClient squarerRestClient;

//    @Retry
    @Timeout(value = 7, unit = ChronoUnit.SECONDS)
    @CircuitBreaker
    public Picture makeItSquare(Picture picture) {
        maybeFail();
        return squarerRestClient.makeItSquarer(picture);
    }

    private void maybeFail() {
//        if (new Random().nextBoolean()) {
            throw new RuntimeException("Resource failure.");
//        }
    }

}
````
This time an artificial failure is always happening in the CDI bean.
We also added a @CircuitBreaker annotation with requestVolumeThreshold = 4. CircuitBreaker.failureRatio is by default 0.5, and CircuitBreaker.delay is by default 5 seconds. 
That means that a circuit breaker will open when 2 of the last 4 invocations failed, and it will stay open for 5 seconds.

Look at Circuit Breaker Maintenance

There is also the possibility to configure Fallbacks to indicate an alternative method to execute in case of error.

### Health check

SmallRye Health allows applications to provide information about their state which is typically useful in cloud environments where automated processes must be able to determine whether the application should be discarded or restarted.
In Quarkus, we use the Smallrye Health extension to configure probes.
Just add the corresponding extension and see what we have by default:

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-health</artifactId>
</dependency>
```

Importing the smallrye-health extension directly exposes three REST endpoints:

- /q/health/live - The application is up and running.
- /q/health/ready - The application is ready to serve requests.
- /q/health/started - The application is started.
- /q/health - Accumulating all health check procedures in the application.

Let's try.

### Metrics

Let's now use the Micrometer metrics library for collecting metrics.

Core micrometer support and runtime integration is provided by `quarkus-micrometer` and other Quarkus and Quarkiverse extensions bring in additional dependencies and requirements to support specific monitoring systems.


#### Add the proper dependencies
We are going to use the quarkus-micrometer and the prometheus extensions.
Add them to the classpath and see what we have by default:

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-micrometer</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-micrometer-registry-prometheus</artifactId>
</dependency>
```
#### Annotate methods

We can now add two annotations to our method: `@Counted` and `@Timed`.

````java
package org.acme;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.temporal.ChronoUnit;
import java.util.Random;

@ApplicationScoped
public class SquarerClient {

    @RestClient
    SquarerRestClient squarerRestClient;

    @Retry
    @Timeout(value = 7, unit = ChronoUnit.SECONDS)
    @CircuitBreaker(requestVolumeThreshold = 4)
    @Counted
    @Timed
    public Picture makeItSquare(Picture picture){
        maybeFail();
        return squarerRestClient.makeItSquarer(picture);
    }

    private void maybeFail() {
        if (new Random().nextBoolean()) {
            throw new RuntimeException("Resource failure.");
        }
    }

}

````
`@Counted` counts the number of calls to the method.
The `@Timed` annotation will wrap the execution of it and will emit some basic tags: class, method, and exception.
It's possible to configure some extra tags on the annotation.
Note that many methods, e.g. REST endpoint methods, are counted and timed by the micrometer extension out of the box.

Finally, check the metrics endpoint exposed out of the box. Navigate to [http://localhost:8080/q/metrics](http://localhost:8080/q/metrics)

### Service Discovery with Stork

```xml
    <dependency>
      <groupId>org.instaquarkm</groupId>
      <artifactId>aws-service-discovery</artifactId>
      <version>1.0.0</version>
    </dependency>
```

````properties
#quarkus.rest-client.squarer-function.url=https://vaekn02h31.execute-api.us-east-1.amazonaws.com/stage
quarkus.rest-client.squarer-function.url=stork://squarer
quarkus.stork.squarer.service-discovery.type=aws-api-gateway
````

Finally, add the credentials needed by the Squarer function.

### Prod configuration

So far we have been working in dev mode, with a database running inside a container that Quarkus has automatically raised because of the postgresql dependency. This is de DevService feature.
Now that we are done, we are going to deploy our uploading-service to an Openshift cluster.
Before, we need to set up some configuration for the prod environment. 
Basically we need to configure the prod database.
For that, add the following properties to the application.properties file: 

````properties
%prod.quarkus.datasource.jdbc.url=jdbc:postgresql://upload-database:5432/${database-name}
%prod.quarkus.datasource.username=${database-user}
%prod.quarkus.datasource.password=${database-password}
````

We also need `kubernetes-config` extension in order to use ConfigMaps and Secrets as configuration source. 
Add the extension to the `pom` file:
```bash
quarkus extension add 'kubernetes-config'
````

And the following related properties:

````properties
%prod.quarkus.kubernetes-config.secrets.enabled=true
quarkus.kubernetes-config.secrets=upload-service,aws
````

### Deploy to OpenShift

Without any additional extension, run the following command:

```bash
quarkus deploy openshift --image-build
```

If you add the Openshift extension:

```bash
quarkus extension add 'openshift'
```

The deployment can be triggered from the Dev-UI console by clicking the `Deploy to OpenShift` link.