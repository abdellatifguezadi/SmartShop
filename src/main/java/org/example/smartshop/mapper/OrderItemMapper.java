package org.example.smartshop.mapper;

import org.example.smartshop.dto.response.OrderItemResponse;
import org.example.smartshop.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.nom", target = "productNom")
    OrderItemResponse toResponse(OrderItem orderItem);
}
