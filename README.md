# Security App

- user authentication
- save user in database
- send confirmation email on registration
- grant access to protected endpoints only for authenticated users 
- option forgot password (send email with token)
- option change password
- have tests for all endpoints

## Built with 

- Kotlin
- Maven
- MySQL
- Spring
- Mockito
- JWT

## Requirements:

- Java SDK 11
- Maven (Optional) 
- MySQL (Optional)
- Docker (Optional)

## Installation

```bash
./mvnw springboot:run
```
```bash
./mvnw clean package
```
Run jar file

```bash
java -jar ./target/demo-0.0.1-SNAPSHOT.jar
```

## Run app locally:

Start mysql docker container
```bash
docker-compose up mysql
```
```
docker build
```

