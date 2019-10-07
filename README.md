# Aggregator service

Simple API service that aggregates the power of attorney and cards data from the [json stub](https://github.com/Aldrion/json-stub) and represents it in a new entity. 

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

In order to build and run this service locally you need:

```
- java 11
- kotlin 1.3
- maven 3.x (all artifacts available on central repository) 

```

### Building and running the app

In order to build this project you need to run 

```
mvn clean install 
```
in root of the project. Another option is opening the project in your favorite IDE and executing same life cycles in maven plugin.

Before running the app make sure that [json stub](https://github.com/Aldrion/json-stub) is already running.

For running of the app you have also two options. First one is running 

```
mvn spring-boot:run
```
in the root of the project. Second is running the main spring boot class from your IDE: AggregatorServiceApplication.kt

Application will start on port 8081 and API will be available at 
[http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)



## Running the tests

While developing tests, guidelines from Martin Fowler [Testing microservices](https://martinfowler.com/articles/microservice-testing/) approach were taken into account.

Unit tests and component tests can be run with command:
```
mvn test
```
For running of the E2E (cucumber) test it is needed that service is started and afterwards started RunCucumberIT.kt test.

NOTE: If this service were communicating with proper APIs and not mocks I would also add one more level of testing - contract testing. For that purpose I would use [spring cloud contract](https://spring.io/projects/spring-cloud-contract)



## Built With

* [kotlin](https://kotlinlang.org/) - Because coroutines are way better than threads...and a lot of other reasons as you can see on my [talk](https://www.youtube.com/watch?v=DBniDC2rhQg) 
* [Spring boot](https://spring.io/projects/spring-boot) - Easiest way to get your service runing
* [Swagger](https://swagger.io/) - Because APIs are read firstly by humans
* [Spring Initializr](https://start.spring.io) - Automate every manual task that you can
* [Cucumber](https://start.spring.io) - BDD framework for proper E2E tests

## Authors

* **Nikola Lucic** - *Initial work* 


