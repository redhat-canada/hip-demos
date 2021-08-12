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

        from("jms:demo?transacted=true").routeId("demo")
                .transacted()
                .log("${body}")
                .process(this::process)
                .to("sql:insert into demo (id, name) values (:#Id, :#Name)?dataSource=postgres")
                .to("sql:insert into demo (id, name) values (:#Id, :#Name)?dataSource=mysql");
    }

    private void process(Exchange exchange) {
        String id = exchange.getIn().getBody(String.class);
        exchange.getIn().setHeader("Id", Integer.parseInt(id));
        exchange.getIn().setHeader("Name", "Hello world");
    }
}

