# spring-http-interfaces

## Overview

This project is a Spring Boot demo that exposes local REST endpoints and fetches user data from the public JSONPlaceholder API.

External API used:
https://jsonplaceholder.typicode.com/users

No authentication is required for this external API.

## What this project demonstrates

- Building REST endpoints with Spring Web MVC.
- Consuming external APIs with Spring RestClient.
- Mapping JSON payloads to Java records.
- Testing controller and client layers with JUnit, Mockito, and Spring Test.

## Technology stack

- Java 25
- Spring Boot 4.0.0
- Spring Web (RestClient + REST controller)
- Maven
- JUnit 5
- Mockito
- Spring Test

## Pre-conditions

Before running the project, make sure you have:

- JDK 25 installed
- JAVA_HOME pointing to JDK 25
- Maven 3.9+
- Internet access to:
    - https://repo.maven.apache.org
    - https://jsonplaceholder.typicode.com

Optional and recommended:

- SDKMAN for managing Java and Maven versions

## Configuration

The external API base URL is configured in application.yml:

api.base.url=https://jsonplaceholder.typicode.com

## API endpoints exposed by this app

- GET /users
- GET /users/{id}

These endpoints delegate to the external API and return the mapped user model.

## How to run

1. Install dependencies and compile:

```bash
mvn clean compile
```

2. Start the application:

```bash
mvn spring-boot:run
```

3. Call local endpoints:

```bash
curl http://localhost:8080/users
curl http://localhost:8080/users/1
```

## How to test

Run unit tests:

```bash
mvn test
```

## Example user payload

```json
{
    "id": 2,
    "name": "Ervin Howell",
    "username": "Antonette",
    "email": "Shanna@melissa.tv",
    "address": {
        "street": "Victor Plains",
        "suite": "Suite 879",
        "city": "Wisokyburgh",
        "zipcode": "90566-7771",
        "geo": {
            "lat": "-43.9509",
            "lng": "-34.4618"
        }
    },
    "phone": "010-692-6593 x09125",
    "website": "anastasia.net",
    "company": {
        "name": "Deckow-Crist",
        "catchPhrase": "Proactive didactic contingency",
        "bs": "synergize scalable supply-chains"
    }
}
```