# WeatherApi

This project is a Weather API application that provides current weather data and hourly weather forecasts. It is built using Spring Boot and integrates with external weather APIs to retrieve data. The project is organized into several packages, each containing specific components that contribute to the functionality of the application.

Technologie Used
.Java
.Spring Boot
.Hibernate
.JUnit
.Docker
.WireMock
.Json
.Quartz Scheduler

## Overview 
The Weather API project is a Spring Boot-based application designed to provide current weather data and hourly weather forecasts for various cities. The application integrates with external weather services to fetch real-time weather information, which is then processed and stored in a local database. Users can access this weather data through RESTful API endpoints.

### Project Structure
.controller: Handles HTTP requests and serves as the entry point for various operations related to cities and weather data.
.dto: Data Transfer Objects used for transferring data between different layers of the application.
.entity: Entity classes that represent the database tables and encapsulate the core data.
.exception: Custom exception classes for handling specific error scenarios.
.repository: Interfaces for database interaction using Spring Data JPA.
.service: Contains business logic implementations that handle operations related to cities and weather data.
.config: Configuration classes that set up application-specific settings, such as job scheduling.
.resources: Contains configuration files and static resources used by the application.
.data.sql: SQL script used to initialize the database with predefined cities or other necessary data.
.test: Contains unit and integration tests to ensure the application is functioning correctly.

WeatherServiceTest: Unit tests for the WeatherService class, verifying business logic related to weather data processing.
TestContainer: Integration tests using TestContainers to test database interactions in an isolated environment.
WeatherApplication: The main entry point for the Spring Boot application, responsible for bootstrapping and running the application

#### Setup

1. Clone this repo
```
git clone https://github.com/SzymonKaczmarek96/WMS.git
```
2.Build the project using Maven
```
mvn clean install
```
```
mvn spring-boot:run
```

3.Docker Setup:
Create and start the Docker containers
```
docker-compose up
```
docker-compose configuartion
```
version: '3.8'

services:
  postgres:
    image: postgres:15
    ports:
    - "5432:5432"
    volumes:
    - db_data:/var/lib/postgresql/data
    environment:
      POSTGRES_PASSWORD: user
      POSTGRES_USER: postgres
      POSTGRES_DB: forecast
volumes:
    db_data:
```
Make sure to replace placeholder values like your_api_key with actual credentials before deploying the application

##### Endpoints

| HTTP Method | URL | Query Parameters | Description |
|-------------|-----|------------------|-------------|
| **City Weather** |
| `GET` | `http://localhost:8000/city/weather` | None | Get the list of all cities |
| `GET` | `http://localhost:8000/city/weather/{cityName}/current` | `forceUpdate` (optional, boolean, default: `false`) | Get the current weather for a specific city by city name. If `forceUpdate=true`, the weather data is forcibly refreshed from the API. |
| `GET` | `http://localhost:8000/city/weather/{cityName}/forecast` | `forceUpdate` (optional, boolean, default: `false`) | Get the weather forecast for a specific city by city name. If `forceUpdate=true`, the forecast data is forcibly refreshed from the API. |
| `GET` | `http://localhost:8000/city/weather/current` | None | Get the current weather for all cities. This retrieves the latest weather data stored in the database. |
| `GET` | `http://localhost:8000/city/weather/hourly` | None | Get the weather forecast for all cities. This retrieves the latest forecast data stored in the database. |
