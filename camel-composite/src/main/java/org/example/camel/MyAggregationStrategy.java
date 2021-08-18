package org.example.camel;

import org.apache.camel.Exchange;

class MyAggregationStrategy implements org.apache.camel.processor.aggregate.AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange;
        }
        String newBody = newExchange.getIn().getBody(String.class);
        String oldBody = oldExchange.getIn().getBody(String.class);
        newBody = oldBody.concat(" + ").concat(newBody);
        newExchange.getIn().setBody(newBody);
        return newExchange;
    }
}
