# camel-composite
```
docker-compose up
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
