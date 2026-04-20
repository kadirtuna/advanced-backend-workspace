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
 * Runs the payment through the chain, then saves the result to the database.
 */
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentHandler paymentChain;
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

        PaymentResponseDTO result = paymentChain.handle(request);

        // save to DB regardless of whether it succeeded or failed
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
