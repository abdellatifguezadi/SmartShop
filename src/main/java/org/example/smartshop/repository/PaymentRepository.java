package org.example.smartshop.repository;

import org.example.smartshop.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderId(Long orderId);

    Long countByOrderId(Long orderId);
}
