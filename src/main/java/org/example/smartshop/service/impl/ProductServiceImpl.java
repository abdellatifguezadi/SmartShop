package org.example.smartshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.smartshop.dto.request.ProductRequest;
import org.example.smartshop.dto.request.ProductUpdateRequest;
import org.example.smartshop.dto.response.ProductResponse;
import org.example.smartshop.entity.Product;
import org.example.smartshop.exception.BusinessException;
import org.example.smartshop.mapper.ProductMapper;
import org.example.smartshop.repository.ProductRepository;
import org.example.smartshop.service.IProductService;
import org.example.smartshop.specification.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByNom(request.getNom())) {
            throw new BusinessException("Un produit avec ce nom existe deja");
        }

        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Produit non trouve"));

        if (productRepository.existsByNom(request.getNom()) && request.getNom() != null && !request.getNom().equals(product.getNom())){
            throw new BusinessException("Un produit avec ce nom existe deja");
        }

        productMapper.updateEntityFromDto(request, product);
        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Produit non trouve"));

        if (product.getDeleted()) {
            throw new BusinessException("Ce produit est deja supprime");
        }

        product.setDeleted(true);
        productRepository.save(product);
    }

    @Override
    public Page<ProductResponse> getAllProducts(String nom, BigDecimal prixMin, BigDecimal prixMax,
                                                 Integer stockMin, Integer stockMax, Pageable pageable) {
        Specification<Product> spec = ProductSpecification. withFilters(nom, prixMin, prixMax, stockMin, stockMax);
        Page<Product> products = productRepository.findAll(spec, pageable);
        return products.map(productMapper::toResponse);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Produit non trouve"));
        if (product.getDeleted()) {
            throw new BusinessException("Ce produit est supprime");
        }
        return productMapper.toResponse(product);
    }
}
