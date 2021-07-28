package org.example.camel;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class ClientService extends RouteBuilder {

    @Override
    public void configure() {
        from("timer:demo?period=1s").routeId("consumer")
                    .to("http4:localhost:8080/api/products?socketTimeout=1000")
                .log("${body}");
    }

    private void fallback(Exchange exchange) {
        exchange.getIn().setBody("Fallback data");
    }
}

//    from("timer:demo?period=1s").routeId("consumer")
//        .hystrix()
//        .hystrixConfiguration()
//        .executionTimeoutInMilliseconds(1000) // an execution timeout for actions inside the hystrix block
//        .circuitBreakerRequestVolumeThreshold(2) //the circuit breaker request volume threshold. This is the minimum number of requests that must be received a minimum before the circuit can be opened due to failures
//        .metricsRollingPercentileWindowInMilliseconds(60000) //  the duration of percentile rolling window in milliseconds. This is period of time that is analyzed to determine if the circuit should be opened due to failures.
//        .circuitBreakerSleepWindowInMilliseconds(15000) // the time in milliseconds after the circuit breaker trips open that it should wait before trying requests again
//        .circuitBreakerErrorThresholdPercentage(50) // the error percentage threshold (as whole number such as 50) at which point the circuit breaker will trip open and reject requests
//        .end()
//        .to("http4:localhost:8080/api/products?socketTimeout=1000")
//                    .onFallback().process(this::fallback)
//        .endHystrix()
//        .log("${body}");