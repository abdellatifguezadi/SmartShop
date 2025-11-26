package org.example.smartshop.service;

import org.example.smartshop.dto.request.ClientRequest;
import org.example.smartshop.dto.request.ClientUpdateRequest;
import org.example.smartshop.dto.response.ClientResponse;

public interface IClientService {
    ClientResponse create(ClientRequest request);
    ClientResponse update(Long id, ClientUpdateRequest request);
    ClientResponse getById(Long id);
    ClientResponse getMyProfile(Long userId);
}
