package com.homework.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Returned to the frontend after a payment attempt.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {

    private Long id;
    private String transactionId;
    private String paymentMethod;
    private BigDecimal amount;
    private String currency;

    private String status; // SUCCESS | FAILED | PENDING
    private String message;
    private String description;
    private LocalDateTime createdAt;

    // used inside the chain to decide whether to continue to the next handler
    private boolean success;

    public static PaymentResponseDTO failure(String message) {
        return PaymentResponseDTO.builder()
                .success(false)
                .status("FAILED")
                .message(message)
                .build();
    }

    public static PaymentResponseDTO pending() {
        return PaymentResponseDTO.builder()
                .success(true)
                .status("PENDING")
                .build();
    }
}
