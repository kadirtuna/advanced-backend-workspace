package com.homework.payment.chain;

import com.homework.payment.dto.PaymentRequestDTO;
import com.homework.payment.dto.PaymentResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Chain Step 2 - checks for suspicious activity before we process anything.
 * Keeps fraud logic separate from validation and processing.
 */
@Slf4j
@Component
public class FraudDetectionHandler extends PaymentHandler {

    private static final BigDecimal HIGH_RISK_THRESHOLD = new BigDecimal("10000.00");

    @Override
    protected PaymentResponseDTO doHandle(PaymentRequestDTO request) {
        log.info("[Chain Step 2] Running fraud detection for {} {} via {}",
                request.getAmount(), request.getCurrency(), request.getPaymentMethod());

        // just log high amounts, don't block them
        if (request.getAmount().compareTo(HIGH_RISK_THRESHOLD) > 0) {
            log.warn("[Chain Step 2] High-risk amount detected: {} — flagged for review",
                    request.getAmount());
        }

        if ("CREDIT_CARD".equals(request.getPaymentMethod())) {
            if (request.getPaymentDetails() == null ||
                    !request.getPaymentDetails().containsKey("cardNumber") ||
                    request.getPaymentDetails().get("cardNumber").isBlank()) {
                log.warn("[Chain Step 2] Fraud check failed: credit card number missing.");
                return PaymentResponseDTO.failure("Credit card number is required.");
            }
        }

        if ("PAYPAL".equals(request.getPaymentMethod())) {
            if (request.getPaymentDetails() == null ||
                    !request.getPaymentDetails().containsKey("email") ||
                    request.getPaymentDetails().get("email").isBlank()) {
                log.warn("[Chain Step 2] Fraud check failed: PayPal email missing.");
                return PaymentResponseDTO.failure("PayPal account email is required.");
            }
        }

        log.info("[Chain Step 2] Fraud detection passed.");
        return PaymentResponseDTO.pending();
    }
}
