package com.homework.payment.service;

import com.homework.payment.dto.PaymentMethodInfoDTO;
import com.homework.payment.dto.PaymentRequestDTO;
import com.homework.payment.dto.PaymentResponseDTO;

import java.util.List;

/**
 * Business Logic Layer interface.
 *
 * SOLID — Dependency Inversion Principle (DIP):
 *   The controller depends on this abstraction, not on PaymentServiceImpl.
 *   This allows swapping implementations without touching the controller.
 */
public interface PaymentService {

    /**
     * Processes a payment through the Chain of Responsibility.
     */
    PaymentResponseDTO processPayment(PaymentRequestDTO request);

    /**
     * Returns all payment records ordered by creation date (newest first).
     */
    List<PaymentResponseDTO> getAllPayments();

    /**
     * Returns a single payment record by its database ID.
     */
    PaymentResponseDTO getPaymentById(Long id);

    /**
     * Returns metadata for all registered payment providers.
     * Data is sourced from the Reflection-based registry.
     */
    List<PaymentMethodInfoDTO> getAvailablePaymentMethods();
}
