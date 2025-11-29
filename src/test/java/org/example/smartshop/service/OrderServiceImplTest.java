package org.example.smartshop.service;

import org.example.smartshop.dto.request.OrderItemRequest;
import org.example.smartshop.dto.request.OrderRequest;
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
import org.example.smartshop.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PromoCodeRepository promoCodeRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Client testClient;
    private Product testProduct;
    private Order testOrder;
    private OrderRequest orderRequest;
    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        testClient = Client.builder()
                .id(1L)
                .nom("Test Client")
                .email("test@test.com")
                .niveauFidelite(CustomerTier.BASIC)
                .totalOrders(0)
                .totalSpent(BigDecimal.ZERO)
                .build();

        testProduct = Product.builder()
                .id(1L)
                .nom("Test Product")
                .description("Test Description")
                .prixUnitaire(new BigDecimal("100.00"))
                .stockDisponible(20)
                .deleted(false)
                .build();

        testOrder = Order.builder()
                .id(1L)
                .client(testClient)
                .statut(OrderStatus.PENDING)
                .sousTotal(new BigDecimal("200.00"))
                .montantRemise(BigDecimal.ZERO)
                .montantHTApresRemise(new BigDecimal("200.00"))
                .montantTVA(new BigDecimal("40.00"))
                .totalTTC(new BigDecimal("240.00"))
                .montantRestant(new BigDecimal("240.00"))
                .tauxTVA(new BigDecimal("20"))
                .dateCreation(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        OrderItemRequest itemRequest = OrderItemRequest.builder()
                .productId(1L)
                .quantite(2)
                .build();

        orderRequest = OrderRequest.builder()
                .clientId(1L)
                .items(Arrays.asList(itemRequest))
                .tauxTVA(new BigDecimal("20"))
                .build();

        orderResponse = new OrderResponse();
        orderResponse.setId(1L);
        orderResponse.setStatut(OrderStatus.PENDING);
    }

    @Test
    void createOrder_WithValidData() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderMapper.toEntity(any(OrderRequest.class))).thenReturn(testOrder);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);

        OrderResponse result = orderService.createOrder(orderRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(OrderStatus.PENDING, result.getStatut());
        assertEquals(new BigDecimal("200.00"), testOrder.getSousTotal());
        assertEquals(BigDecimal.ZERO.setScale(2), testOrder.getMontantRemise().setScale(2));
        assertEquals(new BigDecimal("200.00"), testOrder.getMontantHTApresRemise());
        assertEquals(new BigDecimal("40.00"), testOrder.getMontantTVA());
        assertEquals(new BigDecimal("240.00"), testOrder.getTotalTTC());
        assertEquals(new BigDecimal("240.00"), testOrder.getMontantRestant());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_ClientNotFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            orderService.createOrder(orderRequest);
        });

        assertEquals("Client non trouve avec l'ID: 1", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_ProductNotFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        when(orderMapper.toEntity(any(OrderRequest.class))).thenReturn(testOrder);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            orderService.createOrder(orderRequest);
        });

        assertEquals("Produit non trouve avec l'ID: 1", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_InsufficientStock() {
        testProduct.setStockDisponible(1);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderMapper.toEntity(any(OrderRequest.class))).thenReturn(testOrder);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.createOrder(orderRequest);
        });

        assertEquals("Stock insuffisant pour le produit: Test Product", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_WithPromoCode() {
        PromoCode promoCode = PromoCode.builder()
                .id(1L)
                .code("PROMO-TEST")
                .discountPercentage(new BigDecimal("0.10"))
                .used(false)
                .build();

        orderRequest.setCodePromo("PROMO-TEST");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(promoCodeRepository.findByCode("PROMO-TEST")).thenReturn(Optional.of(promoCode));
        when(orderMapper.toEntity(any(OrderRequest.class))).thenReturn(testOrder);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);

        OrderResponse result = orderService.createOrder(orderRequest);

        assertNotNull(result);
        assertTrue(promoCode.getUsed());
        assertEquals(new BigDecimal("200.00"), testOrder.getSousTotal());
        assertEquals(new BigDecimal("20.00"), testOrder.getMontantRemise());
        assertEquals(new BigDecimal("180.00"), testOrder.getMontantHTApresRemise());
        assertEquals(new BigDecimal("36.00"), testOrder.getMontantTVA());
        assertEquals(new BigDecimal("216.00"), testOrder.getTotalTTC());
        assertEquals(new BigDecimal("216.00"), testOrder.getMontantRestant());
        verify(promoCodeRepository, times(1)).save(promoCode);
    }

    @Test
    void createOrder_PromoCodeAlreadyUsed() {
        PromoCode promoCode = PromoCode.builder()
                .id(1L)
                .code("PROMO-TEST")
                .discountPercentage(new BigDecimal("0.10"))
                .used(true)
                .build();

        orderRequest.setCodePromo("PROMO-TEST");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(promoCodeRepository.findByCode("PROMO-TEST")).thenReturn(Optional.of(promoCode));
        when(orderMapper.toEntity(any(OrderRequest.class))).thenReturn(testOrder);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.createOrder(orderRequest);
        });

        assertEquals("Ce code promo a deja ete utilise", exception.getMessage());
    }

    @Test
    void createOrder_WithSilverTier() {
        testClient.setNiveauFidelite(CustomerTier.SILVER);

        OrderItemRequest itemRequest = OrderItemRequest.builder()
                .productId(1L)
                .quantite(6)
                .build();
        orderRequest.setItems(Arrays.asList(itemRequest));

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderMapper.toEntity(any(OrderRequest.class))).thenReturn(testOrder);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);

        OrderResponse result = orderService.createOrder(orderRequest);

        assertNotNull(result);
        assertEquals(new BigDecimal("600.00"), testOrder.getSousTotal());
        assertEquals(new BigDecimal("30.00"), testOrder.getMontantRemise());
        assertEquals(new BigDecimal("570.00"), testOrder.getMontantHTApresRemise());
        assertEquals(new BigDecimal("114.00"), testOrder.getMontantTVA());
        assertEquals(new BigDecimal("684.00"), testOrder.getTotalTTC());
        assertEquals(new BigDecimal("684.00"), testOrder.getMontantRestant());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_WithGoldTier() {
        testClient.setNiveauFidelite(CustomerTier.GOLD);

        OrderItemRequest itemRequest = OrderItemRequest.builder()
                .productId(1L)
                .quantite(10)
                .build();
        orderRequest.setItems(Arrays.asList(itemRequest));

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderMapper.toEntity(any(OrderRequest.class))).thenReturn(testOrder);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);

        OrderResponse result = orderService.createOrder(orderRequest);

        assertNotNull(result);
        assertEquals(new BigDecimal("1000.00"), testOrder.getSousTotal());
        assertEquals(new BigDecimal("100.00"), testOrder.getMontantRemise());
        assertEquals(new BigDecimal("900.00"), testOrder.getMontantHTApresRemise());
        assertEquals(new BigDecimal("180.00"), testOrder.getMontantTVA());
        assertEquals(new BigDecimal("1080.00"), testOrder.getTotalTTC());
        assertEquals(new BigDecimal("1080.00"), testOrder.getMontantRestant());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_WithPlatinumTier() {
        testClient.setNiveauFidelite(CustomerTier.PLATINUM);

        OrderItemRequest itemRequest = OrderItemRequest.builder()
                .productId(1L)
                .quantite(15)
                .build();
        orderRequest.setItems(Arrays.asList(itemRequest));

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderMapper.toEntity(any(OrderRequest.class))).thenReturn(testOrder);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);

        OrderResponse result = orderService.createOrder(orderRequest);

        assertNotNull(result);
        assertEquals(new BigDecimal("1500.00"), testOrder.getSousTotal());
        assertEquals(new BigDecimal("225.00"), testOrder.getMontantRemise());
        assertEquals(new BigDecimal("1275.00"), testOrder.getMontantHTApresRemise());
        assertEquals(new BigDecimal("255.00"), testOrder.getMontantTVA());
        assertEquals(new BigDecimal("1530.00"), testOrder.getTotalTTC());
        assertEquals(new BigDecimal("1530.00"), testOrder.getMontantRestant());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_WithPromoCodeAndTierDiscount() {
        testClient.setNiveauFidelite(CustomerTier.SILVER);

        PromoCode promoCode = PromoCode.builder()
                .id(1L)
                .code("PROMO-TEST")
                .discountPercentage(new BigDecimal("0.10"))
                .used(false)
                .build();

        orderRequest.setCodePromo("PROMO-TEST");

        OrderItemRequest itemRequest = OrderItemRequest.builder()
                .productId(1L)
                .quantite(6)
                .build();
        orderRequest.setItems(Arrays.asList(itemRequest));

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(promoCodeRepository.findByCode("PROMO-TEST")).thenReturn(Optional.of(promoCode));
        when(orderMapper.toEntity(any(OrderRequest.class))).thenReturn(testOrder);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);

        OrderResponse result = orderService.createOrder(orderRequest);

        assertNotNull(result);
        assertTrue(promoCode.getUsed());
        assertEquals(new BigDecimal("600.00"), testOrder.getSousTotal());
        assertEquals(new BigDecimal("87.00"), testOrder.getMontantRemise());
        assertEquals(new BigDecimal("513.00"), testOrder.getMontantHTApresRemise());
        assertEquals(new BigDecimal("102.60"), testOrder.getMontantTVA());
        assertEquals(new BigDecimal("615.60"), testOrder.getTotalTTC());
        assertEquals(new BigDecimal("615.60"), testOrder.getMontantRestant());
        verify(promoCodeRepository, times(1)).save(promoCode);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void getOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderMapper.toResponse(testOrder)).thenReturn(orderResponse);

        OrderResponse result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderById_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            orderService.getOrderById(1L);
        });

        assertEquals("Commande non trouvee avec l'ID: 1", exception.getMessage());
    }

    @Test
    void getAllOrders() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findAll()).thenReturn(orders);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);

        List<OrderResponse> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getMyOrders() {
        Long userId = 1L;
        List<Order> orders = Arrays.asList(testOrder);

        when(clientRepository.findByUserId(userId)).thenReturn(Optional.of(testClient));
        when(orderRepository.findByClientId(testClient.getId())).thenReturn(orders);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);

        List<OrderResponse> result = orderService.getMyOrders(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(clientRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getMyOrders_ClientNotFound() {
        Long userId = 1L;
        when(clientRepository.findByUserId(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            orderService.getMyOrders(userId);
        });

        assertEquals("Client non trouve", exception.getMessage());
    }

    @Test
    void deleteOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).delete(testOrder);
    }

    @Test
    void deleteOrder_NotPending() {
        testOrder.setStatut(OrderStatus.CONFIRMED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.deleteOrder(1L);
        });

        assertEquals("Impossible de supprimer une commande non en attente", exception.getMessage());
        verify(orderRepository, never()).delete(any(Order.class));
    }

    @Test
    void deleteOrder_WithPromoCode() {
        PromoCode promoCode = PromoCode.builder()
                .id(1L)
                .code("PROMO-TEST")
                .used(true)
                .build();
        
        testOrder.setPromoCode(promoCode);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        orderService.deleteOrder(1L);

        assertFalse(promoCode.getUsed());
        verify(promoCodeRepository, times(1)).save(promoCode);
        verify(orderRepository, times(1)).delete(testOrder);
    }

    @Test
    void cancelOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);

        OrderResponse result = orderService.cancelOrder(1L);

        assertNotNull(result);
        assertEquals(OrderStatus.CANCELED, testOrder.getStatut());
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    void cancelOrder_NotPending() {
        testOrder.setStatut(OrderStatus.CONFIRMED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.cancelOrder(1L);
        });

        assertEquals("Seules les commandes en attente (PENDING) peuvent etre annulees. Statut actuel: CONFIRMED", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void cancelOrder_WithPromoCode() {
        PromoCode promoCode = PromoCode.builder()
                .id(1L)
                .code("PROMO-TEST")
                .used(true)
                .build();
        
        testOrder.setPromoCode(promoCode);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);

        OrderResponse result = orderService.cancelOrder(1L);

        assertNotNull(result);
        assertFalse(promoCode.getUsed());
        verify(promoCodeRepository, times(1)).save(promoCode);
    }

    @Test
    void confirmOrder() {
        testOrder.setStatut(OrderStatus.VALIDATED);
        testOrder.setMontantRestant(BigDecimal.ZERO);

        Payment payment = Payment.builder()
                .id(1L)
                .montant(new BigDecimal("240.00"))
                .statut(PaymentStatus.ENCAISSE)
                .order(testOrder)
                .build();

        testOrder.setPayments(Arrays.asList(payment));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);

        OrderResponse result = orderService.confirmOrder(1L);

        assertNotNull(result);
        assertEquals(OrderStatus.CONFIRMED, testOrder.getStatut());
        assertEquals(1, testClient.getTotalOrders());
        assertEquals(new BigDecimal("240.00"), testClient.getTotalSpent());
        verify(orderRepository, times(1)).save(testOrder);
        verify(clientRepository, times(1)).save(testClient);
    }

    @Test
    void confirmOrder_NotValidated() {
        testOrder.setStatut(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.confirmOrder(1L);
        });

        assertEquals("Seules les commandes validées (VALIDATED) peuvent être confirmées. Statut actuel: PENDING", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void confirmOrder_RemainingAmount() {
        testOrder.setStatut(OrderStatus.VALIDATED);
        testOrder.setMontantRestant(new BigDecimal("100.00"));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.confirmOrder(1L);
        });

        assertEquals("La commande ne peut être confirmée que si elle est totalement payée. Montant restant: 100.00 DH", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void confirmOrder_NoPayments() {
        testOrder.setStatut(OrderStatus.VALIDATED);
        testOrder.setMontantRestant(BigDecimal.ZERO);
        testOrder.setPayments(new ArrayList<>());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.confirmOrder(1L);
        });

        assertEquals("Aucun paiement trouvé pour cette commande", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void confirmOrder_PaymentsNotEncaisse() {
        testOrder.setStatut(OrderStatus.VALIDATED);
        testOrder.setMontantRestant(BigDecimal.ZERO);

        Payment payment = Payment.builder()
                .id(1L)
                .montant(new BigDecimal("240.00"))
                .statut(PaymentStatus.EN_ATTENTE)
                .order(testOrder)
                .build();

        testOrder.setPayments(Arrays.asList(payment));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.confirmOrder(1L);
        });

        assertEquals("Tous les paiements doivent être encaissés avant de confirmer la commande", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void confirmOrder_UpgradesClientTier() {
        testOrder.setStatut(OrderStatus.VALIDATED);
        testOrder.setMontantRestant(BigDecimal.ZERO);
        testOrder.setTotalTTC(new BigDecimal("1200.00"));

        testClient.setTotalOrders(2);
        testClient.setTotalSpent(new BigDecimal("800.00"));

        Payment payment = Payment.builder()
                .id(1L)
                .montant(new BigDecimal("1200.00"))
                .statut(PaymentStatus.ENCAISSE)
                .order(testOrder)
                .build();

        testOrder.setPayments(Arrays.asList(payment));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);

        OrderResponse result = orderService.confirmOrder(1L);

        assertNotNull(result);
        assertEquals(3, testClient.getTotalOrders());
        assertEquals(new BigDecimal("2000.00"), testClient.getTotalSpent());
        assertEquals(CustomerTier.SILVER, testClient.getNiveauFidelite());
        verify(clientRepository, times(1)).save(testClient);
    }
}

