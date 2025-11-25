package org.example.smartshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.smartshop.dto.request.ClientRequest;
import org.example.smartshop.dto.response.ClientResponse;
import org.example.smartshop.entity.Client;
import org.example.smartshop.entity.User;
import org.example.smartshop.enums.CustomerTier;
import org.example.smartshop.enums.UserRole;
import org.example.smartshop.exception.BusinessException;
import org.example.smartshop.mapper.ClientMapper;
import org.example.smartshop.repository.ClientRepository;
import org.example.smartshop.repository.UserRepository;
import org.example.smartshop.service.IClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {
    
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ClientMapper clientMapper;
    
    @Override
    @Transactional
    public ClientResponse create(ClientRequest request) {
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email déjà utilisé");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("Username déjà utilisé");
        }
        
        User user = User.builder()
            .username(request.getUsername())
            .password(request.getPassword())
            .role(UserRole.CLIENT)
            .build();
        
        Client client = Client.builder()
            .nom(request.getNom())
            .email(request.getEmail())
            .niveauFidelite(CustomerTier.BASIC)
            .user(user)
            .build();
        
        user.setClient(client);
        userRepository.save(user);
        
        return clientMapper.toResponse(client);
    }
}
