package com.homework.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for payment responses.
 * Carries the result back to the presentation layer.
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

    /**
     * Outcome: SUCCESS | FAILED | PENDING
     */
    private String status;

    private String message;
    private String description;
    private LocalDateTime createdAt;

    /**
     * Used internally by the Chain of Responsibility to indicate
     * whether the current step succeeded and processing should continue.
     */
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
