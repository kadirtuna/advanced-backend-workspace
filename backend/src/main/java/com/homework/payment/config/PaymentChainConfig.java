package com.homework.payment.config;

import com.homework.payment.chain.FraudDetectionHandler;
import com.homework.payment.chain.PaymentHandler;
import com.homework.payment.chain.PaymentProcessingHandler;
import com.homework.payment.chain.ValidationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the Chain of Responsibility at application startup.
 *
 * Chain order:
 *   ValidationHandler → FraudDetectionHandler → PaymentProcessingHandler
 *
 * The chain head is exposed as a Spring @Bean so that PaymentServiceImpl
 * depends on the abstraction (PaymentHandler), not on concrete handlers.
 * This satisfies the Dependency Inversion Principle (DIP).
 *
 * To add a new step (e.g. RateLimitHandler), insert it here — no other
 * configuration or class needs to change.
 */
@Configuration
public class PaymentChainConfig {

    @Bean
    public PaymentHandler paymentChain(
            ValidationHandler validationHandler,
            FraudDetectionHandler fraudDetectionHandler,
            PaymentProcessingHandler paymentProcessingHandler
    ) {
        // Build the chain: Validation → FraudDetection → Processing
        validationHandler
                .setNext(fraudDetectionHandler)
                .setNext(paymentProcessingHandler);

        return validationHandler; // return the head of the chain
    }
}
