package org.example.smartshop.service;

import org.example.smartshop.dto.request.OrderRequest;
import org.example.smartshop.dto.response.OrderResponse;

public interface IOrderService {
    OrderResponse createOrder(OrderRequest request);
    OrderResponse getOrderById(Long id);
}

