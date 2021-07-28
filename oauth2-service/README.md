# oauth2-service
See https://quarkus.io/guides/security-keycloak-authorization
```
mvn clean package
java -Dquarkus.http.port=8081 -jar target/quarkus-app/quarkus-run.jar
java -Dquarkus.http.port=8082 -jar target/quarkus-app/quarkus-run.jar
```
```
docker run -p 8180:8080 -p 8543:8443 -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin quay.io/keycloak/keycloak:14.0.0
```
```
export access_token=$(\
    curl --insecure -X POST https://localhost:8543/auth/realms/quarkus/protocol/openid-connect/token \
    --user backend-service:secret \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d 'username=alice&password=alice&grant_type=password' | jq --raw-output '.access_token' \
 )
```
```
curl -v GET  http://localhost:8081/api/users/me -H "Authorization: Bearer "$access_token
```
```
export access_token=$(\
    curl --insecure -X POST https://localhost:8543/auth/realms/quarkus/protocol/openid-connect/token \
    --user backend-service:secret \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d 'username=admin&password=admin&grant_type=password' | jq --raw-output '.access_token' \
 )
```
```
curl -v GET  http://localhost:8081/api/admin -H "Authorization: Bearer "$access_token
```
