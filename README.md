# Phone Booking App

The project is setup with [maven](http://maven.apache.org/). 

## Tests

Tests can be found in `src/test/java`. There are 4 main classes to look at.

### Service 

`APhoneBookServiceTests` is an abstract class that defines all the tests to be run with all different implementations of
`PhoneBookService`. `JpaPhoneBookServiceTests` extends `APhoneBookServiceTests` and uses an implementation of `PhoneBookService`
that relies on a Spring Data JPA (Java Persistence API).

### Controller

The class `PhoneBookingControllerTests` contains test related to the REST API. It makes sure that the rest endpoints are 
functional and can respond to http request coming from any http client. 

## Run locally without Docker

### Prerequisites

In order to build and run the server, you will need:
- [Java JDK](https://www.oracle.com/java/) >= 17
- Latest stable [Apache Maven](http://maven.apache.org/)

## Server

- Install prerequisites (see above)
- Launch the project
```bash
./mvnw spring-boot:run
```

Server address is: `http://localhost:8080`

## Run locally with Docker

Build the image on your computer, execute the following command in the root directory of the project. Name of the image is: `phone-booking-app`
```
docker build -t phone-booking-app -f Dockerfile .
```

Run it in a container, it will start automatically the server. Port 8080 of host is mapped to 8080 of container.
```
docker run -it -p 8080:8080 --name phone-booking-app phone-booking-app
```

You can consume the REST API via host. See CURL section below. 

## CURL 

Once the server is up and running, you can interact with it via curl commands. [Basic authentication](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication#basic_authentication_scheme)
is setup to simulate different users. Available users are `peter` and `paul`. They have the same password: `1234`. 

To retrieve the list of phones as `peter`
```
curl http://localhost:8080/api/phones --user "peter:1234"     
```

To book the phone named `Oneplus 9` as `peter` 
```
curl -X POST http://localhost:8080/api/book -H "Content-Type: application/json" -d 'Oneplus 9' --user "peter:1234"     
```

To return the phone named `Oneplus 9` as `peter` 
```
curl -X POST http://localhost:8080/api/return -H "Content-Type: application/json" -d 'Oneplus 9' --user "peter:1234"     
```