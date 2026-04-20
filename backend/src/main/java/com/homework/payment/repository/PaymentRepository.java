package com.homework.payment.repository;

import com.homework.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Layer.
 * Spring Data JPA automatically implements CRUD operations.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByOrderByCreatedAtDesc();

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByPaymentMethodOrderByCreatedAtDesc(String paymentMethod);

    List<Payment> findByStatusOrderByCreatedAtDesc(String status);
}
