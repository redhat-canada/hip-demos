package org.example.camel;

import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.jta.JtaTransactionManager;

@Configuration
public class TransactionConfig {

    @Bean("PROPAGATION_REQUIRES_NEW")
    public SpringTransactionPolicy transactionPolicyPropagationRequired(
            @Autowired JtaTransactionManager transactionManager) {
        SpringTransactionPolicy policy = new SpringTransactionPolicy(transactionManager);
        policy.setPropagationBehaviorName("PROPAGATION_REQUIRES_NEW");
        return policy;
    }

}