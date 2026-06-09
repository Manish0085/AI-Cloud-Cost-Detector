package com.example.cloud.service;

import com.example.cloud.dto.AuthResponse;
import com.example.cloud.dto.CurrentUserResponse;
import com.example.cloud.dto.LoginRequest;
import com.example.cloud.dto.RegisterRequest;
import org.springframework.security.core.Authentication;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    CurrentUserResponse getCurrentLoggedInUser(Authentication authentication);
}
