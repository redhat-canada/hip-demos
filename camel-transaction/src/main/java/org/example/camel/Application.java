package org.example.camel;

import org.apache.camel.component.jms.JmsComponent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.jms.ConnectionFactory;


@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean(name = "jms-component")
    public JmsComponent jmsComponent(ConnectionFactory xaJmsConnectionFactory, PlatformTransactionManager jtaTransactionManager) {
        JmsComponent jms = new JmsComponent();
        jms.setConnectionFactory(xaJmsConnectionFactory);
        jms.setTransactionManager(jtaTransactionManager);
        jms.setTransacted(true);
        return jms;
    }

}