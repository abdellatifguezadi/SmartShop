package org.example.smartshop.service;

import org.example.smartshop.dto.request.ProductRequest;
import org.example.smartshop.dto.request.ProductUpdateRequest;
import org.example.smartshop.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface IProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductUpdateRequest request);
    void deleteProduct(Long id);
    Page<ProductResponse> getAllProducts(String nom, BigDecimal prixMin, BigDecimal prixMax,
                                         Integer stockMin, Integer stockMax, Pageable pageable);
    ProductResponse getProductById(Long id);
}
