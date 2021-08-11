# camel-saga
```
docker-compose --project-directory=infra up 
```
```
docker-compose --project-directory=infra run activemq bash

/opt/amq/bin/artemis queue create --name=demo --address=demo --anycast --durable --preserve-on-no-consumers --auto-create-address --url tcp://activemq:61616
```
```
mvn spring-boot:run
```