package com.homework.payment.strategy;

import com.homework.payment.dto.PaymentRequestDTO;
import com.homework.payment.dto.PaymentResponseDTO;

/**
 * Each payment method (credit card, PayPal, etc.) implements this interface.
 * The chain calls processPayment() after validation and fraud checks pass.
 */
public interface PaymentStrategy {

    PaymentResponseDTO processPayment(PaymentRequestDTO request);
}
