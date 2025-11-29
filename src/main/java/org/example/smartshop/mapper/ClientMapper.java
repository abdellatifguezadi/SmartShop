package org.example.smartshop.mapper;

import org.example.smartshop.dto.request.ClientRequest;
import org.example.smartshop.dto.request.ClientUpdateRequest;
import org.example.smartshop.dto.response.ClientResponse;
import org.example.smartshop.entity.Client;
import org.example.smartshop.entity.Order;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.Comparator;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    
    Client toEntity(ClientRequest request);
    
    @Mapping(target = "datePremiereCommande", expression = "java(getDatePremiereCommande(client))")
    @Mapping(target = "dateDerniereCommande", expression = "java(getDateDerniereCommande(client))")
    ClientResponse toResponse(Client client);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ClientUpdateRequest request, @MappingTarget Client client);

    default LocalDateTime getDatePremiereCommande(Client client) {
        if (client.getOrders() == null || client.getOrders().isEmpty()) {
            return null;
        }
        return client.getOrders().stream()
                .map(Order::getDateCreation)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    default LocalDateTime getDateDerniereCommande(Client client) {
        if (client.getOrders() == null || client.getOrders().isEmpty()) {
            return null;
        }
        return client.getOrders().stream()
                .map(Order::getDateCreation)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }
}
