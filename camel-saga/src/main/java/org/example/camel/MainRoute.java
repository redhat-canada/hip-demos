package org.example.camel;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MainRoute extends RouteBuilder {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void configure() throws Exception {
        getContext().addService(new org.apache.camel.impl.saga.InMemorySagaService());

        from("jms:demo?transacted=true").routeId("demo")
                .process(this::setTransactionData)
                .wireTap("direct:start").newExchange(this::setBamStartData)
                .saga()
                .completion("direct:completion").option("Id", body()).option("JMSMessageID", header("JMSMessageID"))
                .compensation("direct:compensation").option("Id", body()).option("JMSMessageID", header("JMSMessageID"))
                    .to("direct:postgres")
                    .to("direct:mysql");

        from("direct:postgres").routeId("postgres")
                .saga()
                .propagation(SagaPropagation.MANDATORY)
                .compensation("direct:cancelPostgres").option("Id", header("Id"))
                    .to("sql:insert into demo (id, name) values (:#Id, :#Name)?dataSource=postgres")
                    .log("Stored ${header.id} to postgres");

        from("direct:cancelPostgres").routeId("postgres-compensation")
                .to("sql:update demo set name = 'Canceled' where id = :#Id?dataSource=postgres")
                .log("Canceled ${header.id} in postgres");

        from("direct:mysql").routeId("mysql")
                .saga()
                .propagation(SagaPropagation.MANDATORY)
                .compensation("direct:cancelMysql").option("Id", header("Id"))
                    .to("sql:insert into demo (id, name) values (:#Id, :#Name)?dataSource=mysql")
                    .log("Stored ${header.id} to mysql");

        from("direct:cancelMysql").routeId("mysql-compensation")
                .to("sql:update demo set name = 'Canceled' where id = :#Id?dataSource=mysql")
                .log("Canceled ${header.id} in mysql");


        from("direct:start").routeId("bam-start")
                .saga().propagation(SagaPropagation.NOT_SUPPORTED)
                .log("Log start: ${header.Id}")
                .to("sql:INSERT INTO BAM (ID, NAME) VALUES (:#Id, :#Name)?dataSource=bam");

        from("direct:completion").routeId("completion")
                .log("Log Completion: ${header.Id}")
                .process(this::setBamFinishData)
                .to("sql:INSERT INTO BAM (ID, NAME) VALUES (:#Id, :#Name)?dataSource=bam");

        from("direct:compensation").routeId("compensation")
                .log("Log Compensation: ${header.Id}")
                .process(this::setBamFinishData)
                .idempotentConsumer(header("JMSMessageID"), MemoryIdempotentRepository.memoryIdempotentRepository(2000))
                .log("BAM finish: ${header.Id}")
                .to("sql:INSERT INTO BAM (ID, NAME) VALUES (:#Id, :#Name)?dataSource=bam");
    }

    private void setTransactionData(Exchange exchange) {
        String id = exchange.getIn().getBody(String.class);
        exchange.getIn().setHeader("Message", id);
        exchange.getIn().setHeader("Id", Integer.parseInt(id));
        exchange.getIn().setHeader("Name", "Received");
    }

    private void setBamStartData(Exchange exchange) {
        exchange.getIn().setHeader("Name", "Start: " + exchange.getIn().getHeader("Id"));
        exchange.getIn().setHeader("Id", exchange.getIn().getHeader("JMSMessageID") + "0");
    }

    private void setBamFinishData(Exchange exchange) {
        exchange.getIn().setHeader("Name", "Finish: " + exchange.getIn().getHeader("Id"));
        exchange.getIn().setHeader("Id", exchange.getIn().getHeader("JMSMessageID") + "1");
    }
}

