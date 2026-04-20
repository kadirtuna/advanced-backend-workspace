package com.homework.payment.service.impl;

import com.homework.payment.chain.PaymentHandler;
import com.homework.payment.dto.PaymentMethodInfoDTO;
import com.homework.payment.dto.PaymentRequestDTO;
import com.homework.payment.dto.PaymentResponseDTO;
import com.homework.payment.entity.Payment;
import com.homework.payment.exception.PaymentException;
import com.homework.payment.registry.PaymentStrategyRegistry;
import com.homework.payment.repository.PaymentRepository;
import com.homework.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business Logic Layer implementation.
 *
 * Orchestrates the payment flow:
 *   1. Passes the request to the head of the Chain of Responsibility.
 *   2. If successful, persists the Payment entity to PostgreSQL.
 *   3. Returns the result DTO to the controller.
 *
 * SOLID — Single Responsibility Principle (SRP):
 *   Business logic coordination only; no HTTP, no SQL, no payment execution.
 *
 * SOLID — Dependency Inversion Principle (DIP):
 *   Depends on PaymentHandler (abstract) and PaymentService (interface),
 *   not on concrete chain handlers or strategy classes.
 */
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentHandler paymentChain;          // Head of the CoR chain
    private final PaymentRepository paymentRepository;
    private final PaymentStrategyRegistry registry;

    public PaymentServiceImpl(
            PaymentHandler paymentChain,
            PaymentRepository paymentRepository,
            PaymentStrategyRegistry registry
    ) {
        this.paymentChain = paymentChain;
        this.paymentRepository = paymentRepository;
        this.registry = registry;
    }

    @Override
    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        log.info("PaymentService: Starting payment flow for method={}, amount={} {}",
                request.getPaymentMethod(), request.getAmount(), request.getCurrency());

        // Pass through the full Chain of Responsibility:
        // Validation → Fraud Detection → Payment Processing
        PaymentResponseDTO result = paymentChain.handle(request);

        // Persist the outcome regardless of success/failure
        Payment payment = Payment.builder()
                .paymentMethod(request.getPaymentMethod())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .description(request.getDescription())
                .status(result.getStatus())
                .transactionId(result.getTransactionId())
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("PaymentService: Persisted payment record id={}, status={}", saved.getId(), saved.getStatus());

        result.setId(saved.getId());
        result.setCreatedAt(saved.getCreatedAt());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getAllPayments() {
        return paymentRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentException("Payment not found with id: " + id));
        return toDTO(payment);
    }

    @Override
    public List<PaymentMethodInfoDTO> getAvailablePaymentMethods() {
        return registry.getAvailableProviders();
    }

    private PaymentResponseDTO toDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .transactionId(payment.getTransactionId())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .description(payment.getDescription())
                .createdAt(payment.getCreatedAt())
                .success("SUCCESS".equals(payment.getStatus()))
                .build();
    }
}
