package com.homework.payment.strategy;

import com.homework.payment.annotation.PaymentProvider;
import com.homework.payment.dto.PaymentRequestDTO;
import com.homework.payment.dto.PaymentResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * Handles credit card payments.
 * Picked up automatically by the registry because of the @PaymentProvider annotation.
 */
@Slf4j
@Component
@PaymentProvider(
        name = "CREDIT_CARD",
        displayName = "Credit Card",
        description = "Pay securely with Visa, Mastercard or Amex"
)
public class CreditCardPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        log.info("Processing Credit Card payment of {} {}", request.getAmount(), request.getCurrency());

        Map<String, String> details = request.getPaymentDetails();
        String cardNumber = details != null ? details.getOrDefault("cardNumber", "") : "";
        String maskedCard = maskCardNumber(cardNumber);

        String transactionId = "CC-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        log.info("Credit Card payment successful. TxID: {}, Card: {}", transactionId, maskedCard);

        return PaymentResponseDTO.builder()
                .success(true)
                .status("SUCCESS")
                .transactionId(transactionId)
                .paymentMethod("CREDIT_CARD")
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .message("Credit card payment processed successfully. Card: " + maskedCard)
                .description(request.getDescription())
                .build();
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "**** **** **** ****";
        }
        String digits = cardNumber.replaceAll("\\s+", "");
        return "**** **** **** " + digits.substring(Math.max(0, digits.length() - 4));
    }
}
