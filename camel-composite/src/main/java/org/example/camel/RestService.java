package org.example.camel;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestService extends RouteBuilder {

    private static final String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJjZklBRE5feHhDSm1Wa1d5Ti1QTlhFRXZNVVdzMnI2OEN4dG1oRUROelhVIn0.eyJleHAiOjE2Mjc1MTQwODksImlhdCI6MTYyNzUxMTA4OSwianRpIjoiYTBmZDY5ZjAtZTFiNS00NTk1LWEzYmEtMWM0M2EzYmE4ZTJiIiwiaXNzIjoiaHR0cHM6Ly9sb2NhbGhvc3Q6ODU0My9hdXRoL3JlYWxtcy9xdWFya3VzIiwic3ViIjoiYWYxMzRjYWItZjQxYy00Njc1LWIxNDEtMjA1Zjk3NWRiNjc5IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYmFja2VuZC1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6IjRjOTNmNWM4LTdjMzYtNDU4OS1iMTIxLWIzZGNkNTA0YmFjNiIsImFjciI6IjEiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6ImFkbWluIn0.E4bL7IbiuULfdnubXDhcweuho4YcDoWtmUP7Ki1AmWbTRKXi0CbtjWBStC3_lLZL0E4ivA5m6sNc27S6j8DObOb3BfUqT5qZSKyokCvcVJBGkI97-s_EKsuSxyVPOJ5qWCWWY5Yb_snSCb86_4t85SvyIAlKfDQG0R3nCPavprjXHD546UY37H1p8wGQPnItELBhfVcHmwG_rPZkFKuQM4IbjfsEcoqsfa_5PruoojWNxRVhBLnC3qpBTNZSbuu-UzlsWJ2LXYeHY_3r9rZYfS8XovlzLGZGXibcqaajDIKPGoFiAu6wPI2VH0QMvYjaULx23-XvAfsrSMHVSX-dzw";

    @Override
    public void configure() {
        restConfiguration("undertow")
                .port("8080")
                .contextPath("api")
                .bindingMode(RestBindingMode.auto)
                .apiContextPath("api-doc")
                .enableCORS(true);

        rest("/demo")
                .get()
                .to("direct:demo");

        from("direct:demo").routeId("demo")
                .multicast(new MyAggregationStrategy())
                .parallelProcessing().timeout(1000).to("direct:service1", "direct:service2")
                .end()
                .to("kafka:demo?brokers=localhost:29092");

        from("direct:service1").routeId("service1")
                .removeHeader(Exchange.HTTP_URI)
                .setHeader("Authorization", constant("Bearer " + token))
                .log("Call service1")
                .to("http4:localhost:8081/api/users/me")
                .convertBodyTo(String.class)
                .log("${body}");

        from("direct:service2").routeId("service2")
                .removeHeader(Exchange.HTTP_URI)
                .setHeader("Authorization", constant("Bearer " + token))
                .log("Call service2")
                .to("http4:localhost:8082/api/admin")
                .convertBodyTo(String.class)
                .log("${body}");
    }

    private class MyAggregationStrategy implements org.apache.camel.processor.aggregate.AggregationStrategy {
        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            if (oldExchange == null){
                return newExchange;
            }
            String newBody = newExchange.getIn().getBody(String.class);
            String oldBody = oldExchange.getIn().getBody(String.class);
            newBody = newBody.concat(" + ").concat(oldBody);
            newExchange.getIn().setBody(newBody);
            return newExchange;
        }
    }
}

