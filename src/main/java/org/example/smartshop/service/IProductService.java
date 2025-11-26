package org.example.smartshop.service;

import org.example.smartshop.dto.request.ProductRequest;
import org.example.smartshop.dto.response.ProductResponse;

public interface IProductService {
    ProductResponse createProduct(ProductRequest request);
}

