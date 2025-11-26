package org.example.smartshop.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartshop.dto.request.ClientRequest;
import org.example.smartshop.dto.request.ClientUpdateRequest;
import org.example.smartshop.dto.response.ClientResponse;
import org.example.smartshop.service.IClientService;
import org.example.smartshop.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {
    
    private final IClientService clientService;
    
    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody ClientRequest request, HttpSession session) {
        SecurityUtils.requireAdmin(session);
        return ResponseEntity.ok(clientService.create(request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> update(@PathVariable Long id, @Valid @RequestBody ClientUpdateRequest request, HttpSession session) {
        SecurityUtils.requireAdmin(session);
        return ResponseEntity.ok(clientService.update(id, request));
    }
}
