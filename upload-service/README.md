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
