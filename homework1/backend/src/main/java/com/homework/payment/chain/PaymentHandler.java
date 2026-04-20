package com.homework.payment.chain;

import com.homework.payment.dto.PaymentRequestDTO;
import com.homework.payment.dto.PaymentResponseDTO;

/**
 * Base class for the Chain of Responsibility pattern.
 * Each handler does its own job and passes the request to the next one if
 * everything is fine.
 * Chain order: ValidationHandler -> PaymentProcessingHandler
 */
public abstract class PaymentHandler {

    private PaymentHandler next;

    // returns next so we can chain: a.setNext(b).setNext(c)
    public PaymentHandler setNext(PaymentHandler next) {
        this.next = next;
        return next;
    }

    /**
     * Runs this handler, then forwards to the next one if successful.
     */
    public final PaymentResponseDTO handle(PaymentRequestDTO request) {
        PaymentResponseDTO result = doHandle(request);

        if (!result.isSuccess()) {
            return result;
        }

        if (next != null) {
            return next.handle(request);
        }

        return result;
    }

    // return success=false to stop the chain, success=true to continue
    protected abstract PaymentResponseDTO doHandle(PaymentRequestDTO request);
}
