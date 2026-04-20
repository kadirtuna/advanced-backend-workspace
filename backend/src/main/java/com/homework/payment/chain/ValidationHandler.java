package com.homework.payment.chain;

import com.homework.payment.dto.PaymentRequestDTO;
import com.homework.payment.dto.PaymentResponseDTO;
import com.homework.payment.registry.PaymentStrategyRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Chain Step 1 — Input Validation.
 *
 * Validates that the payment request is complete and coherent:
 * - Required fields are present
 * - Amount is positive and within limits
 * - Payment method is registered in the system
 *
 * SRP: This handler's sole responsibility is request validation.
 */
@Slf4j
@Component
public class ValidationHandler extends PaymentHandler {

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("50000.00");

    private final PaymentStrategyRegistry registry;

    public ValidationHandler(PaymentStrategyRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected PaymentResponseDTO doHandle(PaymentRequestDTO request) {
        log.info("[Chain Step 1] Validating payment request — method: {}, amount: {} {}",
                request.getPaymentMethod(), request.getAmount(), request.getCurrency());

        // Validate payment method is registered
        if (!registry.isSupported(request.getPaymentMethod())) {
            log.warn("Validation failed: unsupported payment method '{}'", request.getPaymentMethod());
            return PaymentResponseDTO.failure(
                    "Unsupported payment method: '" + request.getPaymentMethod() + "'"
            );
        }

        // Validate amount
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Validation failed: invalid amount {}", request.getAmount());
            return PaymentResponseDTO.failure("Payment amount must be greater than zero.");
        }

        if (request.getAmount().compareTo(MAX_AMOUNT) > 0) {
            log.warn("Validation failed: amount {} exceeds limit {}", request.getAmount(), MAX_AMOUNT);
            return PaymentResponseDTO.failure(
                    "Payment amount exceeds the maximum allowed limit of " + MAX_AMOUNT + "."
            );
        }

        log.info("[Chain Step 1] Validation passed.");
        return PaymentResponseDTO.pending();
    }
}
