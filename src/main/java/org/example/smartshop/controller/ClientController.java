package org.example.smartshop.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartshop.dto.request.ClientRequest;
import org.example.smartshop.dto.request.ClientUpdateRequest;
import org.example.smartshop.dto.response.ClientResponse;
import org.example.smartshop.dto.response.ClientStatisticsResponse;
import org.example.smartshop.service.IClientService;
import org.example.smartshop.util.SecurityUtils;
import org.springframework.boot.web.server.servlet.WebListenerRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {
    
    private final IClientService clientService;
    
    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody ClientRequest request, HttpSession session) {
        SecurityUtils.requireAdmin(session);
        return ResponseEntity.ok(clientService.createClient(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> update(@PathVariable Long id, @Valid @RequestBody ClientUpdateRequest request, HttpSession session) {
        SecurityUtils.requireAdmin(session);
        return ResponseEntity.ok(clientService.updateClient(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable Long id , HttpSession session){
        SecurityUtils.requireAdmin(session);
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @GetMapping("/profile")
    public ResponseEntity<ClientResponse> getMyProfile(HttpSession session) {
        SecurityUtils.requireClient(session);
        Long userId = SecurityUtils.getAuthenticatedUserId(session);
        return ResponseEntity.ok(clientService.getMyProfile(userId));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ClientStatisticsResponse> getMyStatistics(HttpSession session) {
        SecurityUtils.requireClient(session);
        Long userId = SecurityUtils.getAuthenticatedUserId(session);
        return ResponseEntity.ok(clientService.getMyStatistics(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpSession session) {
        SecurityUtils.requireAdmin(session);
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> getAllClient(HttpSession session){
        SecurityUtils.requireAdmin(session);
        List<ClientResponse> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }



}
