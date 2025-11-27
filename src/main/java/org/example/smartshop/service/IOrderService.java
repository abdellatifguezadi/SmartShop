package org.example.smartshop.service;

import org.example.smartshop.dto.request.OrderRequest;
import org.example.smartshop.dto.response.OrderResponse;

import java.util.List;

public interface IOrderService {
    OrderResponse createOrder(OrderRequest request);
    OrderResponse getOrderById(Long id);
    List<OrderResponse> getAllOrders();
    void deleteOrder(Long id);
}
