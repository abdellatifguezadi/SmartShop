package org.example.smartshop.service;

import org.example.smartshop.dto.request.ClientRequest;
import org.example.smartshop.dto.request.ClientUpdateRequest;
import org.example.smartshop.dto.response.ClientResponse;
import org.example.smartshop.dto.response.ClientStatisticsResponse;

public interface IClientService {
    ClientResponse createClient(ClientRequest request);
    ClientResponse updateClient(Long id, ClientUpdateRequest request);
    ClientResponse getClientById(Long id);
    ClientResponse getMyProfile(Long userId);
    ClientStatisticsResponse getMyStatistics(Long userId);
    void deleteClient(Long id);
}
