package org.example.camel;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MainRoute extends RouteBuilder {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void configure() {
//
//        from("timer:demo?repeatCount=10").routeId("producer")
//                .process(e -> e.getIn().setBody(String.valueOf(counter.incrementAndGet())))
//                .to("activemq:demo");

        from("activemq:demo?transacted=true").routeId("consumer")
                .transacted("policyPropagationRequired")
                .log("${body}")
                .process(this::process)
                .to("sql:insert into demo (id, name) values (:#Id, :#Name)");
    }

    private void process(Exchange exchange) {
        String id = exchange.getIn().getBody(String.class);
        exchange.getIn().setHeader("Id", Integer.parseInt(id));
        exchange.getIn().setHeader("Name", "Hello world");
    }
}

