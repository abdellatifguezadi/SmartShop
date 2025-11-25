package org.example.smartshop.mapper;

import org.example.smartshop.dto.request.ClientRequest;
import org.example.smartshop.dto.request.ClientUpdateRequest;
import org.example.smartshop.dto.response.ClientResponse;
import org.example.smartshop.entity.Client;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    
    Client toEntity(ClientRequest request);
    
    ClientResponse toResponse(Client client);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ClientUpdateRequest request, @MappingTarget Client client);
}
