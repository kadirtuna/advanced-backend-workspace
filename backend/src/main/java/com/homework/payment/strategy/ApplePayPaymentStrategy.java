package com.homework.payment.strategy;

import com.homework.payment.annotation.PaymentProvider;
import com.homework.payment.dto.PaymentRequestDTO;
import com.homework.payment.dto.PaymentResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * Handles Apple Pay payments.
 * Same as the others - just @PaymentProvider and implementing the interface.
 */
@Slf4j
@Component
@PaymentProvider(
        name = "APPLE_PAY",
        displayName = "Apple Pay",
        description = "Fast and secure payment with Face ID or Touch ID"
)
public class ApplePayPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        log.info("Processing Apple Pay payment of {} {}", request.getAmount(), request.getCurrency());

        Map<String, String> details = request.getPaymentDetails();
        String deviceId = details != null ? details.getOrDefault("deviceId", "unknown-device") : "unknown-device";

        String transactionId = "AP-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        log.info("Apple Pay payment successful. TxID: {}, Device: {}", transactionId, maskDeviceId(deviceId));

        return PaymentResponseDTO.builder()
                .success(true)
                .status("SUCCESS")
                .transactionId(transactionId)
                .paymentMethod("APPLE_PAY")
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .message("Apple Pay payment processed successfully.")
                .description(request.getDescription())
                .build();
    }

    private String maskDeviceId(String deviceId) {
        if (deviceId == null || deviceId.length() < 4) return "****";
        return "****-" + deviceId.substring(deviceId.length() - 4);
    }
}
