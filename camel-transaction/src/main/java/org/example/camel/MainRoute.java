package org.example.camel;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MainRoute extends RouteBuilder {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void configure() {

        from("jms:demo?transacted=true").routeId("demo")
                .transacted()
                .log("Main process: ${body}")
                .process(this::setTransactionData)
                .wireTap("direct:start").newExchange(this::setBamStartData)
                .to("sql:insert into demo (id, name) values (:#Id, :#Name)?dataSource=postgres")
                .to("sql:insert into demo (id, name) values (:#Id, :#Name)?dataSource=mysql")
                .wireTap("direct:finish").newExchange(this::setBamFinishData);

        from("direct:start").routeId("bam-start")
                .transacted("PROPAGATION_REQUIRES_NEW")
                .log("BAM start: ${body}")
                .to("sql:insert into bam (id, name) values (:#Id, :#Name)?dataSource=bam");

        from("direct:finish").routeId("bam-finish")
                .transacted("PROPAGATION_REQUIRES_NEW")
                .log("BAM finish: ${body}")
                .to("sql:insert into bam (id, name) values (:#Id, :#Name)?dataSource=bam");
    }

    private void setTransactionData(Exchange exchange) {
        String id = exchange.getIn().getBody(String.class);
        exchange.getIn().setHeader("Message", id);
        exchange.getIn().setHeader("Id", Integer.parseInt(id));
        exchange.getIn().setHeader("Name", "Hello world");
    }

    private void setBamStartData(Exchange exchange) {
        exchange.getIn().setHeader("Name", "Start: " + exchange.getIn().getBody(String.class));
        exchange.getIn().setHeader("Id", UUID.randomUUID().toString());
    }

    private void setBamFinishData(Exchange exchange) {
        exchange.getIn().setHeader("Name", "Finish: " + exchange.getIn().getBody(String.class));
        exchange.getIn().setHeader("Id", UUID.randomUUID().toString());
    }
}

