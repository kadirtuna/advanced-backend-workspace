package com.homework.payment.config;

import com.homework.payment.chain.PaymentHandler;
import com.homework.payment.chain.PaymentProcessingHandler;
import com.homework.payment.chain.ValidationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Builds the handler chain and exposes it as a bean.
 * Order: Validation -> Processing
 */
@Configuration
public class PaymentChainConfig {

    @Bean
    public PaymentHandler paymentChain(
            ValidationHandler validationHandler,
            PaymentProcessingHandler paymentProcessingHandler
    ) {
        validationHandler.setNext(paymentProcessingHandler);
        return validationHandler;
    }
}
