package org.example.camel;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestService extends RouteBuilder {

    @Override
    public void configure() {
        restConfiguration("undertow").port("8080").contextPath("api")
                .bindingMode(RestBindingMode.auto).apiContextPath("api-doc").enableCORS(true);

        rest("/demo")
                .get()
                .to("direct:demo");

        from("direct:demo").routeId("demo")
                .multicast(new MyAggregationStrategy())
                .parallelProcessing().timeout(1000).to("direct:service1", "direct:service2")
                .end()
                .pollEnrich("aws-s3://hipdemo?prefix=demo.txt&deleteAfterRead=false", new MyAggregationStrategy())
                .to("kafka:demo?brokers=localhost:29092");

        from("direct:service1").routeId("service1")
                .removeHeader(Exchange.HTTP_URI)
                .setHeader("Authorization", simple("Bearer ${properties:token}"))
                .log("Call service1")
                .to("http4:localhost:8081/api/users/me")
                .convertBodyTo(String.class)
                .log("${body}");

        from("direct:service2").routeId("service2")
                .removeHeader(Exchange.HTTP_URI)
                .setHeader("Authorization", simple("Bearer ${properties:token}"))
                .log("Call service2")
                .to("http4:localhost:8082/api/admin")
                .convertBodyTo(String.class)
                .log("${body}");
    }

}

