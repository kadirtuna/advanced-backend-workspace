package com.homework.payment.strategy;

import com.homework.payment.annotation.PaymentProvider;
import com.homework.payment.dto.PaymentRequestDTO;
import com.homework.payment.dto.PaymentResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * Handles PayPal payments.
 * Added without touching any existing class - just implementing the interface
 * and putting @PaymentProvider on it was enough.
 */
@Slf4j
@Component
@PaymentProvider(
        name = "PAYPAL",
        displayName = "PayPal",
        description = "Pay with your PayPal account balance or linked bank"
)
public class PayPalPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        log.info("Processing PayPal payment of {} {}", request.getAmount(), request.getCurrency());

        Map<String, String> details = request.getPaymentDetails();
        String email = details != null ? details.getOrDefault("email", "unknown@paypal.com") : "unknown@paypal.com";

        String transactionId = "PP-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        log.info("PayPal payment successful. TxID: {}, Account: {}", transactionId, maskEmail(email));

        return PaymentResponseDTO.builder()
                .success(true)
                .status("SUCCESS")
                .transactionId(transactionId)
                .paymentMethod("PAYPAL")
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .message("PayPal payment processed successfully. Account: " + maskEmail(email))
                .description(request.getDescription())
                .build();
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "****@****.com";
        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];
        String maskedName = name.length() > 2
                ? name.substring(0, 2) + "****"
                : "****";
        return maskedName + "@" + domain;
    }
}
