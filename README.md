# spring-http-interfaces

## Overview

This project demonstrates Spring's declarative HTTP interfaces using `RestClient` and `HttpServiceProxyFactory`. It exposes local REST endpoints and fetches user data from the public JSONPlaceholder API.

External API used:
https://jsonplaceholder.typicode.com/users

No authentication is required for this external API.

## What this project demonstrates

- **Declarative HTTP interfaces** with Spring Web annotations
- Building REST endpoints with Spring Web MVC
- Consuming external APIs with Spring `RestClient` and `HttpServiceProxyFactory`
- Mapping JSON payloads to Java records
- Testing controller and HTTP client layers with JUnit, Mockito, and Spring Test

## Architecture

The application uses a single declarative HTTP client backed by Spring's `RestClient`:

```
UserController
      ↓
UserHttpClient (declarative interface)
      ↓
HttpServiceProxyFactory
      ↓
RestClient
      ↓
JSONPlaceholder REST API
```

### Declarative HTTP Interfaces

Spring Framework 6 introduced declarative HTTP interfaces, allowing you to define HTTP clients as simple Java interfaces annotated with Spring's web annotations. This approach is cleaner than manual `RestClient` calls because:

1. **Type-safe**: The interface defines the contract explicitly
2. **Testable**: Easy to mock in unit tests
3. **Maintainable**: The client and controller are clearly separated
4. **Composable**: Can be enhanced with interceptors and error handlers

### How RestClient and HttpServiceProxyFactory work together

1. **RestClient**: A synchronous HTTP client that handles the actual network communication
2. **RestClientAdapter**: Adapts the `RestClient` to Spring's HTTP service interface
3. **HttpServiceProxyFactory**: Creates a proxy implementation of the declarative interface at runtime

Here's how the configuration works:

```java
@Configuration
public class HttpClientConfig {
    @Bean
    public UserHttpClient userHttpClient(@Value("${api.base.url}") String baseUrl) {
        // Create the underlying RestClient
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
        
        // Wrap it in an adapter
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        
        // Create a proxy factory
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(adapter)
                .build();
        
        // Generate the proxy implementation
        return factory.createClient(UserHttpClient.class);
    }
}
```

### The Declarative HTTP Client Interface

```java
@HttpExchange("/users")
public interface UserHttpClient {
    
    @GetExchange
    List<User> findAll();
    
    @GetExchange("/{id}")
    User findById(@PathVariable("id") Integer id);
}
```

Annotations explained:
- `@HttpExchange("/users")`: Sets the base path for all methods
- `@GetExchange`: Maps to HTTP GET (similar to `@RequestMapping(method = GET)`)
- `@PathVariable`: Extracts path segments into method parameters

### Request Flow

1. `UserController` receives an HTTP request at `/users` or `/users/{id}`
2. Controller injects `UserHttpClient` (a proxy created by `HttpServiceProxyFactory`)
3. Controller calls the interface method (e.g., `userHttpClient.findById(id)`)
4. The proxy intercepts the call and:
   - Constructs the HTTP request using the `RestClient`
   - Sends it to the external API
   - Deserializes the JSON response into the Java record
   - Returns the result to the controller
5. Controller serializes the result and sends it back to the client

## Technology stack

- Java 25
- Spring Boot 4.0.0
- Spring Framework 7.0.1
- Spring Web (RestClient, declarative HTTP interfaces, REST controller)
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

The external API base URL is configured in `application.yml`:

```yaml
api:
  base:
    url: https://jsonplaceholder.typicode.com
```

This value is injected into the `HttpClientConfig` and used to create the `RestClient` bean.

## API endpoints exposed by this app

- `GET /users` — Returns all users from the external API
- `GET /users/{id}` — Returns a specific user by ID from the external API

These endpoints delegate to the `UserHttpClient` interface and return the mapped user model.

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

4. Check Actuator endpoints (health, metrics, info):

```bash
curl http://localhost:8080/actuator
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/info
```

5. Expected response (curl http://localhost:8080/users/1):

```json
{
    "id": 1,
    "name": "Leanne Graham",
    "username": "Bret",
    "email": "Sincere@april.biz",
    "address": {
        "street": "Kulas Light",
        "suite": "Apt. 556",
        "city": "Gwenborough",
        "zipcode": "92998-3874",
        "geo": {
            "lat": "-37.3159",
            "lng": "81.1496"
        }
    },
    "phone": "1-770-736-8031 x56442",
    "website": "hildegard.org",
    "company": {
        "name": "Romaguera-Crona",
        "catchPhrase": "Multi-layered client-server neural-net",
        "bs": "harness real-time e-markets"
    }
}
```

## Monitoring with Spring Boot Actuator

This project includes [Spring Boot Actuator](https://docs.spring.io/spring-boot/reference/actuator/index.html) for application monitoring and metrics.

### Exposed endpoints

The following Actuator endpoints are available at `/actuator`:

- `/health` — Application health status
- `/info` — Application information (Java version, OS details)
- `/metrics` — Application metrics (JVM, process, system)
- `/env` — Environment variables and properties
- `/loggers` — Logger configuration and levels

### Accessing Actuator endpoints

Start the application and access endpoints via HTTP:

```bash
# List all available endpoints
curl http://localhost:8080/actuator

# Check application health
curl http://localhost:8080/actuator/health

# View application info
curl http://localhost:8080/actuator/info

# Get detailed metrics
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/metrics/jvm.memory.used
curl http://localhost:8080/actuator/metrics/process.uptime

# View environment properties
curl http://localhost:8080/actuator/env

# List loggers
curl http://localhost:8080/actuator/loggers
```

### Configuration

Actuator is configured in `application.yml`:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,env,loggers
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
  metrics:
    enable:
      jvm: true
      process: true
      system: true
```

Key settings:
- **exposure.include**: Controls which endpoints are exposed
- **base-path**: Base URL path for all Actuator endpoints
- **health.show-details**: Shows detailed health info when authorized
- **metrics.enable**: Enables specific metric categories (JVM, process, system)

## Code formatting with Spotless

This project uses [Spotless](https://github.com/diffplug/spotless) with [Google Java Format 1.30.0](https://github.com/google/google-java-format) to enforce consistent code style across the entire codebase.

### Formatting commands

Check code style compliance:

```bash
mvn spotless:check
```

Automatically format all source code:

```bash
mvn spotless:apply
```

Format code as part of the build:

```bash
mvn clean verify
```

The build includes Spotless checking, so all code must be properly formatted to pass verification.

### Formatting standards

- **Formatter**: Google Java Format 1.30.0 (Java 25 compatible)
- **Import order**: `java` → `javax` → `org` → `com`
- **Unused imports**: Automatically removed
- **Line length**: 100 characters
- **Indentation**: 2 spaces

### IDE integration

Most IDEs support Google Java Format through plugins:

- **IntelliJ IDEA**: Install "Google Java Format" plugin (File → Settings → Plugins)
- **Eclipse**: Use built-in formatter or install Spotless Eclipse plugin
- **VS Code**: Install "Google Java Format for VS Code" extension

## How to test

Run unit tests:

```bash
mvn test
```

Or run the full build including verification:

```bash
mvn clean verify
```

### Test structure

**UserHttpClientTest**: Tests the declarative HTTP client directly using `MockRestServiceServer`:
- Verifies HTTP methods and request URLs
- Verifies JSON deserialization into Java records
- Does not depend on the live JSONPlaceholder API

**UserControllerTest**: Tests the REST controller using `MockMvc`:
- Mocks `UserHttpClient` to simulate different scenarios
- Verifies response status codes and JSON payloads
- Verifies that the correct client methods are invoked

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

## Further reading

- [Spring Framework 7 HTTP Interfaces Documentation](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-http-interface)
- [RestClient Documentation](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-restclient)
- [Spring Web Annotations](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-requestmapping.html)