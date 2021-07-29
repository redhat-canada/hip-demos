# camel-composite
```
docker-compose up
```
```
docker-compose exec broker  kafka-console-consumer --bootstrap-server broker:9092 --topic demo --from-beginning
```
```
    curl --insecure -X POST https://localhost:8543/auth/realms/quarkus/protocol/openid-connect/token \
    --user backend-service:secret \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d 'username=admin&password=admin&grant_type=password' | jq --raw-output '.access_token'
```
```
mvn spring-boot:run
```
```
curl -v GET  http://localhost:8080/api/demo 
```
https://s3.console.aws.amazon.com/s3/buckets/hipdemo?region=us-east-1&tab=objects