package org.example.smartshop.mapper;

import org.example.smartshop.dto.request.PromoCodeRequest;
import org.example.smartshop.dto.response.PromoCodeResponse;
import org.example.smartshop.entity.PromoCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PromoCodeMapper {


    @Mapping(target = "used", constant = "false")
    @Mapping(target = "discountPercentage", constant = "0.05")
    PromoCode toEntity(PromoCodeRequest request);

    PromoCodeResponse toResponse(PromoCode promoCode);
}
