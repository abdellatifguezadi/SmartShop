package org.example.smartshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.smartshop.entity.Order;
import org.example.smartshop.entity.Payment;
import org.example.smartshop.enums.OrderStatus;
import org.example.smartshop.enums.PaymentStatus;
import org.example.smartshop.repository.OrderRepository;
import org.example.smartshop.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderTransactionalService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveOrderAndPaymentAsRejected(Order order, Payment payment) {
        order.setStatut(OrderStatus.REJECTED);
        payment.setStatut(PaymentStatus.REJETE);

        orderRepository.save(order);
        paymentRepository.save(payment);
    }
}

