package org.example.smartshop.service;

import org.example.smartshop.dto.request.LoginRequest;
import org.example.smartshop.dto.response.AuthResponse;

public interface IAuthService {
    AuthResponse login(LoginRequest request);
}
