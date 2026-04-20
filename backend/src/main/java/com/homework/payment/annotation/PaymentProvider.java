package com.homework.payment.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to mark a class as a payment provider.
 * The registry scans for this annotation at startup using reflection
 * and automatically registers any class that has it.
 *
 * To add a new payment method, just implement PaymentStrategy and put this
 * annotation on it.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PaymentProvider {

    String name(); // like "CREDIT_CARD"

    String displayName(); // like "Credit Card"

    String description() default "";
}
