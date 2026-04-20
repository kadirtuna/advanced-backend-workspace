package com.homework.payment.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a payment provider.
 * Used with Java Reflection in PaymentStrategyRegistry to dynamically
 * discover and register all available payment methods at application startup.
 *
 * Adding a new payment method only requires:
 *   1. Implementing PaymentStrategy
 *   2. Annotating the class with @PaymentProvider
 * No other code changes needed — demonstrating Open/Closed Principle.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PaymentProvider {

    /**
     * Unique identifier used in API requests (e.g. "CREDIT_CARD", "PAYPAL").
     */
    String name();

    /**
     * Human-readable display name shown in the UI (e.g. "Credit Card").
     */
    String displayName();

    /**
     * Brief description of the payment method.
     */
    String description() default "";
}
