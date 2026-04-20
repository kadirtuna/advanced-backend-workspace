package com.homework.payment.chain;

import com.homework.payment.dto.PaymentRequestDTO;
import com.homework.payment.dto.PaymentResponseDTO;

/**
 * Abstract base class for the Chain of Responsibility pattern.
 *
 * Each concrete handler:
 *   1. Implements the doHandle() method with its own logic.
 *   2. Calls handle() on the next handler if processing should continue.
 *
 * Chain order:  ValidationHandler → FraudDetectionHandler → PaymentProcessingHandler
 *
 * SOLID — Single Responsibility Principle (SRP):
 *   Each handler has exactly one responsibility (validate, detect fraud, or process).
 *
 * SOLID — Open/Closed Principle (OCP):
 *   New steps (e.g. LoggingHandler, RateLimitHandler) can be inserted
 *   without changing existing handlers.
 */
public abstract class PaymentHandler {

    private PaymentHandler next;

    /**
     * Sets the next handler in the chain and returns it (for fluent chaining).
     */
    public PaymentHandler setNext(PaymentHandler next) {
        this.next = next;
        return next;
    }

    /**
     * Entry point: run this handler's logic, then pass to the next if successful.
     */
    public final PaymentResponseDTO handle(PaymentRequestDTO request) {
        PaymentResponseDTO result = doHandle(request);

        if (!result.isSuccess()) {
            // Chain is broken — return the failure immediately
            return result;
        }

        if (next != null) {
            return next.handle(request);
        }

        return result;
    }

    /**
     * Concrete handlers implement their specific processing step here.
     *
     * @return a response with success=true to continue the chain,
     *         or success=false to halt it with an error.
     */
    protected abstract PaymentResponseDTO doHandle(PaymentRequestDTO request);
}
