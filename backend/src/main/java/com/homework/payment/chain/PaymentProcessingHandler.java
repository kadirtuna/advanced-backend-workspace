package com.homework.payment.chain;

import com.homework.payment.dto.PaymentRequestDTO;
import com.homework.payment.dto.PaymentResponseDTO;
import com.homework.payment.registry.PaymentStrategyRegistry;
import com.homework.payment.strategy.PaymentStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Chain Step 3 — Payment Processing (terminal handler).
 *
 * This is the last handler in the chain. It:
 *   1. Uses PaymentStrategyRegistry to look up the correct strategy via Reflection.
 *   2. Delegates to the strategy's processPayment() method.
 *
 * It never calls setNext() — it is the chain's terminal node.
 *
 * SRP: This handler's sole responsibility is dispatching to the right strategy.
 */
@Slf4j
@Component
public class PaymentProcessingHandler extends PaymentHandler {

    private final PaymentStrategyRegistry registry;

    public PaymentProcessingHandler(PaymentStrategyRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected PaymentResponseDTO doHandle(PaymentRequestDTO request) {
        log.info("[Chain Step 3] Dispatching to payment strategy for method: {}",
                request.getPaymentMethod());

        // Registry uses Reflection to find the right strategy
        PaymentStrategy strategy = registry.getStrategy(request.getPaymentMethod());

        PaymentResponseDTO result = strategy.processPayment(request);

        log.info("[Chain Step 3] Payment strategy returned status: {}, TxID: {}",
                result.getStatus(), result.getTransactionId());

        return result;
    }
}
