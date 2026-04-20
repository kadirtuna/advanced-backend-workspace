package com.homework.payment.service;

import com.homework.payment.dto.PaymentMethodInfoDTO;
import com.homework.payment.dto.PaymentRequestDTO;
import com.homework.payment.dto.PaymentResponseDTO;

import java.util.List;

/**
 * Service layer interface. Controller talks to this, not directly to the implementation.
 */
public interface PaymentService {

    PaymentResponseDTO processPayment(PaymentRequestDTO request);

    List<PaymentResponseDTO> getAllPayments();

    PaymentResponseDTO getPaymentById(Long id);

    List<PaymentMethodInfoDTO> getAvailablePaymentMethods();
}
