package com.homework.payment.controller;

import com.homework.payment.dto.PaymentMethodInfoDTO;
import com.homework.payment.dto.PaymentRequestDTO;
import com.homework.payment.dto.PaymentResponseDTO;
import com.homework.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Presentation Layer — REST API controller.
 *
 * Exposes HTTP endpoints for the payment system.
 * Delegates ALL business logic to PaymentService (DIP).
 *
 * SOLID — Single Responsibility Principle (SRP):
 *   This class handles HTTP concerns only: routing, request parsing,
 *   response serialization, and HTTP status codes.
 */
@Slf4j
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * GET /api/payments/methods
     * Returns all registered payment providers (discovered via Reflection).
     */
    @GetMapping("/methods")
    public ResponseEntity<List<PaymentMethodInfoDTO>> getPaymentMethods() {
        log.info("GET /api/payments/methods");
        return ResponseEntity.ok(paymentService.getAvailablePaymentMethods());
    }

    /**
     * POST /api/payments
     * Processes a payment through the Chain of Responsibility.
     *
     * Example request body:
     * {
     *   "paymentMethod": "CREDIT_CARD",
     *   "amount": 150.00,
     *   "currency": "USD",
     *   "description": "Order #12345",
     *   "paymentDetails": {
     *     "cardNumber": "4111111111111111",
     *     "cardHolderName": "John Doe",
     *     "expiryDate": "12/27",
     *     "cvv": "123"
     *   }
     * }
     */
    @PostMapping
    public ResponseEntity<PaymentResponseDTO> processPayment(
            @Valid @RequestBody PaymentRequestDTO request
    ) {
        log.info("POST /api/payments — method: {}, amount: {} {}",
                request.getPaymentMethod(), request.getAmount(), request.getCurrency());

        PaymentResponseDTO response = paymentService.processPayment(request);

        HttpStatus status = "SUCCESS".equals(response.getStatus())
                ? HttpStatus.CREATED
                : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(response);
    }

    /**
     * GET /api/payments
     * Returns all payment records, newest first.
     */
    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments() {
        log.info("GET /api/payments");
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    /**
     * GET /api/payments/{id}
     * Returns a single payment record.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long id) {
        log.info("GET /api/payments/{}", id);
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }
}
