# Saga pattern with Camel
```

 ┌────────────────────┐           ┌─────────────────────┐
 │                    │           │                     │                     ┌─────────────┐
 │  Postgres database │◄──────────┤ ◄─────┐             │ ◄──── Get message ─ │             │
 │                    │           │       │             │                     │     AMQ     │
 └────────────────────┘           │       Saga          │                     │             │
                                  │       │             │                     └─────────────┘
 ┌────────────────────┐           │       │             │                            ▲
 │                    │◄──────────┤ ◄─────┘             │────────Commit/Rollback─────┘                            
 │  Mysql database    │           │                     │                            
 │                    │           │                     ├
 └────────────────────┘           │                     │
                                  │      Out of Saga    │
                                  │          │          │
                                  │          │          │
                                  └──────────┼──────────┘
                                             │
                                             │
                                             │
                                             │
                                   ┌─────────▼──────────┐
                                   │                    │
                                   │  BAM Database      │
                                   │                    │
                                   └────────────────────┘
 
```
## How to
### Start Postgres, Mysql, AMQ
```
docker-compose --project-directory=infra up 
```
### Create queue
```
docker-compose --project-directory=infra run activemq bash

/opt/amq/bin/artemis queue create --name=demo --address=demo --anycast --durable --preserve-on-no-consumers --auto-create-address --url tcp://activemq:61616
```
### Run Integration
```
mvn spring-boot:run
```

### Demo
1. Open [pgadmin](http://localhost:5050/)
2. Open [mysql adminer](http://localhost:6060/)
3. Open [AMQ console](http://localhost:8161/)

#### Completion case
1. Publish message '1' to demo queue
2. Check demo table in postgres (1 row added)
3. Check demo table in mysql (1 row added)
4. Check bam table in postgres (2 rows added)

#### Compensation case
1. Truncate demo table in postgres
2. Publish message '1' to demo queue
3. Check demo table in postgres (0 rows added)
4. Check demo table in mysql (0 rows added)
5. Check bam table in postgres (2 rows added)
