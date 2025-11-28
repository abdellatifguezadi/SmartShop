package org.example.smartshop.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartshop.dto.request.LoginRequest;
import org.example.smartshop.dto.response.AuthResponse;
import org.example.smartshop.enums.UserRole;
import org.example.smartshop.service.IAuthService;
import org.example.smartshop.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final IAuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        AuthResponse response = authService.login(request);
        SecurityUtils.login(session, response);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(AuthResponse.builder()
            .message("Déconnexion réussie")
            .build());
    }
    
    @GetMapping("/current")
    public ResponseEntity<AuthResponse> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(AuthResponse.builder()
            .userId(userId)
            .username((String) session.getAttribute("username"))
            .role((UserRole) session.getAttribute("role"))
            .message("Session active")
            .build());
    }
}
