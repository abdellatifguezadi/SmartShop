package org.example.smartshop.mapper;

import org.example.smartshop.dto.request.ProductRequest;
import org.example.smartshop.dto.request.ProductUpdateRequest;
import org.example.smartshop.dto.response.ProductResponse;
import org.example.smartshop.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    Product toEntity(ProductRequest request);
    
    ProductResponse toResponse(Product product);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProductUpdateRequest request, @MappingTarget Product product);
}
