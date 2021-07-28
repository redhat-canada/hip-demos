package org.example.camel;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class RestService extends RouteBuilder {

    static List<ProductDto> products = new ArrayList<>();

    static {
        products.add(new ProductDto("product1", "description1"));
        products.add(new ProductDto("product2", "description2"));
    }

    @Override
    public void configure() {
        restConfiguration()
                .component("undertow")
                .port("8080")
                .contextPath("api")
                .bindingMode(RestBindingMode.auto)
                .apiContextPath("api-doc")
                .enableCORS(true);

        rest("/products")
                .get()
                .to("direct:products");

        from("direct:products").routeId("rest-service")
                .log("--------------------")
                .log("service called")
                .log("--------------------")
                .process(RestService::waitDelay)
                .setBody(constant(products))
                .marshal().json(JsonLibrary.Gson);
    }

    public static void waitDelay(Exchange exchange) throws InterruptedException {
        Thread.sleep(1);
    }
}