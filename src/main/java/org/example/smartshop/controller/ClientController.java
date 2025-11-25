package org.example.smartshop.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartshop.dto.request.ClientRequest;
import org.example.smartshop.dto.request.ClientUpdateRequest;
import org.example.smartshop.dto.response.ClientResponse;
import org.example.smartshop.enums.UserRole;
import org.example.smartshop.exception.ForbiddenException;
import org.example.smartshop.exception.UnauthorizedException;
import org.example.smartshop.service.IClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {
    
    private final IClientService clientService;
    
    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody ClientRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new UnauthorizedException("Non authentifié");
        }
        UserRole role = (UserRole) session.getAttribute("role");
        if (role != UserRole.ADMIN) {
            throw new ForbiddenException("Accès réservé aux administrateurs");
        }
        return ResponseEntity.ok(clientService.create(request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> update(@PathVariable Long id, @Valid @RequestBody ClientUpdateRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new UnauthorizedException("Non authentifié");
        }
        UserRole role = (UserRole) session.getAttribute("role");
        if (role != UserRole.ADMIN) {
            throw new ForbiddenException("Accès réservé aux administrateurs");
        }
        return ResponseEntity.ok(clientService.update(id, request));
    }
}
