package com.homework.payment.strategy;

import com.homework.payment.dto.PaymentRequestDTO;
import com.homework.payment.dto.PaymentResponseDTO;

/**
 * Strategy interface for payment processing.
 *
 * SOLID — Interface Segregation Principle (ISP):
 *   Focused solely on payment processing; no unrelated concerns.
 *
 * SOLID — Dependency Inversion Principle (DIP):
 *   Higher-level modules (Service, Chain) depend on this abstraction,
 *   not on concrete payment implementations.
 *
 * SOLID — Open/Closed Principle (OCP):
 *   New payment methods are added by implementing this interface
 *   and annotating with @PaymentProvider — zero changes to existing code.
 */
public interface PaymentStrategy {

    /**
     * Executes the payment logic specific to this payment method.
     *
     * @param request the validated, fraud-checked payment request
     * @return the result of the payment attempt
     */
    PaymentResponseDTO processPayment(PaymentRequestDTO request);
}
