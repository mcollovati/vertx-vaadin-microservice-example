# Vertx-Vaadin microservice example

This repository contains an exmaple of usage of [Vaadin](https://vadin.com) on [Vert.x](https://vertx.io/),
based on [Vert.x - From zero to (micro-) hero workshop](https://github.com/cescoffier/vertx-microservices-workshop).

In addition to the source code from the workshop repository there is a new dashboard written in Vaadin.

To build the project run

```
mvn -Pproduction package
```

To run the example open 5 different terminals and launch the services in the following order:

```
cd quote-generator
java jar target/quote-generator-1.0-SNAPSHOT-fat.jar
```

```
cd portfolio-service
java -jar target/portfolio-service-1.0-SNAPSHOT-fat.jar
```

```
cd compulsive-traders
java -jar target/compulsive-traders-1.0-SNAPSHOT-fat.jar
```

```
cd audit-service
java-jar target/audit-service-1.0-SNAPSHOT-fat.jar
```

```
cd  vaadin-trader-dashboard
java -jar target/vaadin-trader-dashboard-1.0-SNAPSHOT-fat.jar
```


Open your browser and request http://localhost:8080 to see the Vaadin based dashboard.