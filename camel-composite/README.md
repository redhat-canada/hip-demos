# Composite API with Camel


```
 ┌────────────────────┐           ┌─────────────────────┐ 
 │                    │           │                     │ 
 │  REST Service 1    │◄──────────┤ ◄─────┐             │ ◄──── REST API request
 │                    │           │       │             │ 
 └────────────────────┘           │    1. parallel      │
 ┌────────────────────┐           │       │             │ 
 │                    │           │       │             │ 
 │  REST Service 2    │◄──────────┤ ◄─────┘             │ 
 │                    │           │                     │ 
 └────────────────────┘           │    2. aggregate     │
 ┌────────────────────┐           │                     │ 
 │                    │           │                     │ 
 │    SaaS (AWS S3)   │◄──────────┤    3. publish       │ 
 │                    │           │                     │ 
 └────────────────────┘           │                     │ 
 ┌────────────────────┐           │                     │ 
 │                    │           │                     │ 
 │       Kafka        │◄──────────┤     3. enrich       │ 
 │                    │           │                     │ ────► REST API response
 └────────────────────┘           └─────────────────────┘ 
 
```

## How to
### Start Kafka and Keycloak
```
docker─compose up
```
### Start Kafka consumer to view demo topic
```
docker-compose exec broker  kafka-console-consumer --bootstrap-server broker:9092 --topic demo --from-beginning
```
### Create realm in Keycloak
Add new realm from [realm JSON file](../oauth2-service/quarkus-realm.json)

More info :https://www.keycloak.org/docs/latest/server_admin/index.html#_create-realm

### Build and start services
```shell
cd ../oauth2-service
export JAVA_HOME=/Library/Java/JavaVirtualMachines/adoptopenjdk-11.jdk/Contents/Home
mvn clean package
java -Dquarkus.http.port=8081 -jar target/quarkus-app/quarkus-run.jar
java -Dquarkus.http.port=8082 -jar target/quarkus-app/quarkus-run.jar
```

### Get token
Get token
```
    curl --insecure -X POST https://localhost:8543/auth/realms/quarkus/protocol/openid-connect/token \
    --user backend-service:secret \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d 'username=admin&password=admin&grant_type=password' | jq --raw-output '.access_token'
```
Set in application.properties
```text
token=...
```
### Get AWS access and secret keys
Create access key in [AWS AIM Console](https://console.aws.amazon.com/iam/home#/users/demo?section=security_credentials)

Set in application.properties
```text
camel.component.aws-s3.configuration.access-key=...
camel.component.aws-s3.configuration.secret-key=...
```
#### AWS S3 file
https://s3.console.aws.amazon.com/s3/buckets/hipdemo?region=us-east-1&tab=objects

### Run service
```
mvn spring-boot:run
```
### Call service
```
curl -v GET  http://localhost:8080/api/demo 
```

