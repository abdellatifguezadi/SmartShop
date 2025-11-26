package org.example.smartshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.smartshop.dto.request.ClientRequest;
import org.example.smartshop.dto.request.ClientUpdateRequest;
import org.example.smartshop.dto.response.ClientResponse;
import org.example.smartshop.entity.Client;
import org.example.smartshop.entity.User;
import org.example.smartshop.enums.UserRole;
import org.example.smartshop.exception.BusinessException;
import org.example.smartshop.mapper.ClientMapper;
import org.example.smartshop.repository.ClientRepository;
import org.example.smartshop.repository.UserRepository;
import org.example.smartshop.service.IClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {
    
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ClientMapper clientMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ClientResponse create(ClientRequest request) {
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email deja utilise");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("Username deja utilise");
        }
        
        Client client = clientMapper.toEntity(request);
        
        User user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(UserRole.CLIENT)
            .client(client)
            .build();
        
        client.setUser(user);
        userRepository.save(user);
        
        return clientMapper.toResponse(client);
    }
    
    @Override
    @Transactional
    public ClientResponse update(Long id, ClientUpdateRequest request) {
        Client client = clientRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Client non trouve"));
        
        if (request.getEmail() != null && !client.getEmail().equals(request.getEmail()) && 
            clientRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email deja utilise");
        }
        
        User user = client.getUser();
        if (request.getUsername() != null && !user.getUsername().equals(request.getUsername()) && 
            userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("Username deja utilise");
        }
        
        clientMapper.updateEntityFromDto(request, client);
        if (request.getUsername() != null && !request.getUsername().isEmpty()) user.setUsername(request.getUsername());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) user.setPassword(passwordEncoder.encode(request.getPassword()));

        clientRepository.save(client);
        return clientMapper.toResponse(client);
    }

    @Override
    public ClientResponse getById(Long id) {
        Client client = clientRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Client non trouve"));
        return clientMapper.toResponse(client);
    }
}
