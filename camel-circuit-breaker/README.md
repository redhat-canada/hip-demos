# Circuit Breaker with Camel


```
 +--------------------+           +---------------------+ 
 |                    |           |                     | 
 |  REST Service      |<+-------+-+   Client Service    | 
 |                    |           |                     | 
 +--------------------+           +---------------------+ 

```

## How to
### Simple case (no circuit breaker)
Run application
```bash
mvn spring-boot:run
```

### Timeout case (no circuit breaker)
Set timeout 3 sec in RestService.java
```java
    public static void waitDelay(Exchange exchange) throws InterruptedException {
        Thread.sleep(3000);
    }
```
Run application
```bash
mvn spring-boot:run
```

### Timeout case (with circuit breaker)
Keep timeout 3 sec in RestService.java
```java
public static void waitDelay(Exchange exchange) throws InterruptedException {
    Thread.sleep(3000);
}
```
Uncomment Hystrix configuration in ClientService.java 
```java
from("timer:demo?period=1s").routeId("consumer")
        .hystrix()
        .hystrixConfiguration()
        .executionTimeoutInMilliseconds(1000)  an execution timeout for actions inside the hystrix block
        .circuitBreakerRequestVolumeThreshold(2) the circuit breaker request volume threshold. This is the minimum number of requests that must be received a minimum before the circuit can be opened due to failures
        .metricsRollingPercentileWindowInMilliseconds(60000)   the duration of percentile rolling window in milliseconds. This is period of time that is analyzed to determine if the circuit should be opened due to failures.
        .circuitBreakerSleepWindowInMilliseconds(15000)  the time in milliseconds after the circuit breaker trips open that it should wait before trying requests again
        .circuitBreakerErrorThresholdPercentage(50)  the error percentage threshold (as whole number such as 50) at which point the circuit breaker will trip open and reject requests
        .end()
        .to("http4:localhost:8080/api/products?socketTimeout=1000")
        .onFallback().process(this::fallback)
        .endHystrix()
        .log("${body}");
```
Run application
```bash
mvn spring-boot:run
```
