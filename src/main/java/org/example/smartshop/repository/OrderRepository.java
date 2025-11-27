package org.example.smartshop.repository;

import org.example.smartshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByClientId(Long clientId);

    List<Order> findByClientId(Long clientId);
}
