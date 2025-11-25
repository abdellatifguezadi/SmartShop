package org.example.smartshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.smartshop.dto.request.LoginRequest;
import org.example.smartshop.dto.response.AuthResponse;
import org.example.smartshop.entity.User;
import org.example.smartshop.exception.ResourceNotFoundException;
import org.example.smartshop.exception.UnauthorizedException;
import org.example.smartshop.repository.UserRepository;
import org.example.smartshop.service.IAuthService;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Mot de passe incorrect");
        }
        
        return AuthResponse.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .role(user.getRole())
            .message("Connexion réussie")
            .build();
    }
}
