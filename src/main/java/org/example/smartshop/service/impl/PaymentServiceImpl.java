package org.example.smartshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.smartshop.dto.request.PaymentRequest;
import org.example.smartshop.dto.response.PaymentResponse;
import org.example.smartshop.entity.*;
import org.example.smartshop.enums.OrderStatus;
import org.example.smartshop.enums.PaymentMethod;
import org.example.smartshop.enums.PaymentStatus;
import org.example.smartshop.exception.BusinessException;
import org.example.smartshop.exception.ResourceNotFoundException;
import org.example.smartshop.mapper.PaymentMapper;
import org.example.smartshop.repository.OrderRepository;
import org.example.smartshop.repository.PaymentRepository;
import org.example.smartshop.repository.ProductRepository;
import org.example.smartshop.service.IPaymentService;
import org.example.smartshop.validation.PaymentValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PaymentMapper paymentMapper;
    private final OrderTransactionalService orderTransactionalService;
    private final PaymentValidator paymentValidator;

    @Override
    @Transactional
    public PaymentResponse createPayment(Long orderId, PaymentRequest request) {

        paymentValidator.validatePayment(request);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvee avec l'ID: " + orderId));

        if (order.getStatut() != OrderStatus.PENDING && order.getStatut() != OrderStatus.VALIDATED) {
            throw new BusinessException("Impossible d'ajouter un paiement. La commande doit etre en statut PENDING ou VALIDATED. Statut actuel: " + order.getStatut());
        }

        if (request.getMontant().compareTo(order.getMontantRestant()) > 0) {
            throw new BusinessException("Le montant du paiement (" + request.getMontant() + 
                    ") depasse le montant restant de la commande (" + order.getMontantRestant() + ")");
        }

        Payment payment = paymentMapper.toEntity(request);
        payment.setOrder(order);
        payment.setDatePaiement(LocalDateTime.now());

        Long count = paymentRepository.countByOrderId(orderId);
        payment.setNumeroPaiement(count.intValue() + 1);

        payment.setNumeroRecu("REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        //check si le 1er payment ou non
        List<Payment> existingPayments = paymentRepository.findByOrderId(orderId);
        if (existingPayments.isEmpty()) {

            boolean stockSuffisant = verifierStock(order);

            if (!stockSuffisant) {
                orderTransactionalService.saveOrderAndPaymentAsRejected(order, payment);

                throw new BusinessException("Paiement rejeté : Stock insuffisant pour un ou plusieurs produits de cette commande. La commande a été rejetée.");
            } else {
                if (request.getTypePaiement() == PaymentMethod.ESPECES) {
                    payment.setStatut(PaymentStatus.ENCAISSE);
                    payment.setDateEncaissement(LocalDateTime.now());
                } else {
                    payment.setStatut(PaymentStatus.EN_ATTENTE);
                }

                order.setStatut(OrderStatus.VALIDATED);

                deduireStock(order);
            }
        } else {
            if (request.getTypePaiement() == PaymentMethod.ESPECES) {
                payment.setStatut(PaymentStatus.ENCAISSE);
                payment.setDateEncaissement(LocalDateTime.now());
            } else {
                payment.setStatut(PaymentStatus.EN_ATTENTE);
            }
        }

        BigDecimal nouveauMontantRestant = order.getMontantRestant().subtract(request.getMontant());
        order.setMontantRestant(nouveauMontantRestant);

        Payment savedPayment = paymentRepository.save(payment);
        orderRepository.save(order);

        return paymentMapper.toResponse(savedPayment);
    }



    private boolean verifierStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product.getStockDisponible() < item.getQuantite()) {
                return false;
            }
        }
        return true;
    }

    private void deduireStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockDisponible(product.getStockDisponible() - item.getQuantite());
            productRepository.save(product);
        }
    }

    @Override
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouve avec l'ID: " + id));
        return paymentMapper.toResponse(payment);
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    public List<PaymentResponse> getPaymentsByOrderId(Long orderId) {
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        return payments.stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    public PaymentResponse validatePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()-> new ResourceNotFoundException("Paiement non trouve avec l'ID: " + paymentId));

        if(payment.getStatut() != PaymentStatus.EN_ATTENTE){
            throw new BusinessException("La commande doit etre en statut : EN ATTANTE");
        }

        payment.setStatut(PaymentStatus.ENCAISSE);
        paymentRepository.save(payment);
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse rejectedPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouve avec l'ID: " + paymentId));

        if (payment.getStatut() != PaymentStatus.EN_ATTENTE) {
            throw new BusinessException("Le paiement doit etre en statut : EN_ATTENTE");
        }

        payment.setStatut(PaymentStatus.REJETE);
        Payment rejectedPayment = paymentRepository.save(payment);

        Order order = payment.getOrder();
        order.setMontantRestant(order.getMontantRestant().add(payment.getMontant()));

        List<Payment> allPayments = paymentRepository.findByOrderId(order.getId());
        boolean allPaymentsRejected = allPayments.stream()
                .allMatch(p -> p.getStatut() == PaymentStatus.REJETE);

        if (allPaymentsRejected) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setStockDisponible(product.getStockDisponible() + item.getQuantite());
                productRepository.save(product);
            }

            order.setStatut(OrderStatus.PENDING);
        }

        orderRepository.save(order);

        return paymentMapper.toResponse(rejectedPayment);
    }
}
