package org.example.smartshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.smartshop.dto.request.OrderItemRequest;
import org.example.smartshop.dto.request.OrderRequest;
import org.example.smartshop.dto.request.OrderUpdateRequest;
import org.example.smartshop.dto.response.OrderResponse;
import org.example.smartshop.entity.*;
import org.example.smartshop.enums.CustomerTier;
import org.example.smartshop.enums.OrderStatus;
import org.example.smartshop.enums.PaymentStatus;
import org.example.smartshop.exception.BusinessException;
import org.example.smartshop.exception.ResourceNotFoundException;
import org.example.smartshop.mapper.OrderMapper;
import org.example.smartshop.repository.ClientRepository;
import org.example.smartshop.repository.OrderRepository;
import org.example.smartshop.repository.ProductRepository;
import org.example.smartshop.repository.PromoCodeRepository;
import org.example.smartshop.service.IOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouve avec l'ID: " + request.getClientId()));

        Order order = orderMapper.toEntity(request);
        order.setClient(client);
        order.setStatut(OrderStatus.PENDING);

        if (order.getTauxTVA() == null) {
            order.setTauxTVA(new BigDecimal("20"));
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal sousTotal = BigDecimal.ZERO;

        //order item
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produit non trouve avec l'ID: " + itemRequest.getProductId()));

            if (product.getStockDisponible() < itemRequest.getQuantite()) {
                throw new BusinessException("Stock insuffisant pour le produit: " + product.getNom());
            }

            BigDecimal totalLigne = product.getPrixUnitaire()
                    .multiply(new BigDecimal(itemRequest.getQuantite()))
                    .setScale(2, RoundingMode.HALF_UP);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantite(itemRequest.getQuantite())
                    .totalLigne(totalLigne)
                    .build();

            orderItems.add(orderItem);
            sousTotal = sousTotal.add(totalLigne);

        }

        order.setItems(orderItems);
        order.setSousTotal(sousTotal.setScale(2, RoundingMode.HALF_UP));

        // remise if exist
        BigDecimal montantRemise = calculateTierDiscount(client.getNiveauFidelite(), sousTotal);

        BigDecimal montantApresRemiseFidelite = sousTotal.subtract(montantRemise);

        //code promo
        if (request.getCodePromo() != null && !request.getCodePromo().isEmpty()) {
            PromoCode promoCode = promoCodeRepository.findByCode(request.getCodePromo())
                    .orElseThrow(() -> new ResourceNotFoundException("Code promo non trouve: " + request.getCodePromo()));

            if (Boolean.TRUE.equals(promoCode.getUsed())) {
                throw new BusinessException("Ce code promo a deja ete utilise");
            }

            BigDecimal promoDiscount = montantApresRemiseFidelite.multiply(promoCode.getDiscountPercentage())
                    .setScale(2, RoundingMode.HALF_UP);
            montantRemise = montantRemise.add(promoDiscount);

            promoCode.setUsed(true);
            promoCodeRepository.save(promoCode);
            order.setPromoCode(promoCode);
        }

        order.setMontantRemise(montantRemise);
        
        //montant total apres remis
        BigDecimal montantHTApresRemise = sousTotal.subtract(montantRemise)
                .setScale(2, RoundingMode.HALF_UP);

        order.setMontantHTApresRemise(montantHTApresRemise);
        
        //calcule de tva
        BigDecimal montantTVA = montantHTApresRemise
                .multiply(order.getTauxTVA())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        order.setMontantTVA(montantTVA);
        
        //montant total
        BigDecimal totalTTC = montantHTApresRemise.add(montantTVA)
                .setScale(2, RoundingMode.HALF_UP);
        order.setTotalTTC(totalTTC);

        order.setMontantRestant(totalTTC);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvee avec l'ID: " + id));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(Long userId) {
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouve"));

        List<Order> orders = orderRepository.findByClientId(client.getId());
        return orders.stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvee avec l'ID: " + id));

        if(!order.getStatut().equals(OrderStatus.PENDING)){
            throw new BusinessException("Impossible de supprimer une commande non en attente");
        }

        if (order.getPromoCode() != null && Boolean.TRUE.equals(order.getPromoCode().getUsed())) {
            PromoCode promoCode = order.getPromoCode();
            promoCode.setUsed(false);
            promoCodeRepository.save(promoCode);
        }

        orderRepository.delete(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvee avec l'ID: " + id));

        if (order.getStatut() != OrderStatus.PENDING) {
            throw new BusinessException("Seules les commandes en attente (PENDING) peuvent etre annulees. Statut actuel: " + order.getStatut());
        }

        if (order.getPromoCode() != null && Boolean.TRUE.equals(order.getPromoCode().getUsed())) {
            PromoCode promoCode = order.getPromoCode();
            promoCode.setUsed(false);
            promoCodeRepository.save(promoCode);
        }

        order.setStatut(OrderStatus.CANCELED);
        Order canceledOrder = orderRepository.save(order);

        return orderMapper.toResponse(canceledOrder);
    }



    @Override
    @Transactional
    public OrderResponse confirmOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvee avec l'ID: " + id));

        if (order.getStatut() != OrderStatus.VALIDATED) {
            throw new BusinessException("Seules les commandes validées (VALIDATED) peuvent être confirmées. Statut actuel: " + order.getStatut());
        }

        if (order.getMontantRestant().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessException("La commande ne peut être confirmée que si elle est totalement payée. Montant restant: " + order.getMontantRestant() + " DH");
        }

        List<Payment> payments = order.getPayments();
        if (payments == null || payments.isEmpty()) {
            throw new BusinessException("Aucun paiement trouvé pour cette commande");
        }

        List<Payment> activePayments = payments.stream()
                .filter(payment -> payment.getStatut() != PaymentStatus.REJETE)
                .toList();

        if (activePayments.isEmpty()) {
            throw new BusinessException("Aucun paiement valide trouvé pour cette commande");
        }

        boolean allPaymentsEncaisse = activePayments.stream()
                .allMatch(payment -> payment.getStatut() == PaymentStatus.ENCAISSE);

        if (!allPaymentsEncaisse) {
            throw new BusinessException("Tous les paiements doivent être encaissés avant de confirmer la commande");
        }

        order.setStatut(OrderStatus.CONFIRMED);
        Order confirmedOrder = orderRepository.save(order);

        Client client = order.getClient();
        client.setTotalOrders(client.getTotalOrders() + 1);
        client.setTotalSpent(client.getTotalSpent().add(order.getTotalTTC()));

        CustomerTier newTier = calculateCustomerTier(client.getTotalOrders(), client.getTotalSpent());
        client.setNiveauFidelite(newTier);

        clientRepository.save(client);

        return orderMapper.toResponse(confirmedOrder);
    }

    private CustomerTier calculateCustomerTier(Integer totalOrders, BigDecimal totalSpent) {
        if (totalOrders >= 20 || totalSpent.compareTo(new BigDecimal("15000")) >= 0) {
            return CustomerTier.PLATINUM;
        }

        if (totalOrders >= 10 || totalSpent.compareTo(new BigDecimal("5000")) >= 0) {
            return CustomerTier.GOLD;
        }

        if (totalOrders >= 3 || totalSpent.compareTo(new BigDecimal("1000")) >= 0) {
            return CustomerTier.SILVER;
        }

        return CustomerTier.BASIC;
    }

    private BigDecimal calculateTierDiscount(CustomerTier tier, BigDecimal sousTotal) {
        BigDecimal discountPercentage = BigDecimal.ZERO;

        switch (tier) {
            case SILVER:
                if (sousTotal.compareTo(new BigDecimal("500")) >= 0) {
                    discountPercentage = new BigDecimal("5");
                }
                break;
            case GOLD:
                if (sousTotal.compareTo(new BigDecimal("800")) >= 0) {
                    discountPercentage = new BigDecimal("10");
                }
                break;
            case PLATINUM:
                if (sousTotal.compareTo(new BigDecimal("1200")) >= 0) {
                    discountPercentage = new BigDecimal("15");
                }
                break;
            case BASIC:
            default:
                discountPercentage = BigDecimal.ZERO;
                break;
        }

        return sousTotal.multiply(discountPercentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }
}
