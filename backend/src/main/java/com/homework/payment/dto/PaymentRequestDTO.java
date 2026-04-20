package com.homework.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Data Transfer Object for incoming payment requests.
 * Keeps the presentation layer decoupled from the domain entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {

    /**
     * Payment method identifier (e.g. "CREDIT_CARD", "PAYPAL", "APPLE_PAY").
     * Must match a registered @PaymentProvider name.
     */
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
    private String currency;

    private String description;

    /**
     * Method-specific details supplied by the client.
     * Examples:
     *   CREDIT_CARD: { cardNumber, cardHolderName, expiryDate, cvv }
     *   PAYPAL:      { email }
     *   APPLE_PAY:   { deviceId, token }
     */
    private Map<String, String> paymentDetails;
}
