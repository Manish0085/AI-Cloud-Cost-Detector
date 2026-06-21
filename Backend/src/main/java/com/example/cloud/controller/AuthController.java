package com.example.cloud.controller;

import com.example.cloud.dto.AuthResponse;
import com.example.cloud.dto.CurrentUserResponse;
import com.example.cloud.dto.LoginRequest;
import com.example.cloud.dto.RegisterRequest;
import com.example.cloud.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "Authentication APIs for registration, login and current user details"
)
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns a JWT token"
    )
    @ApiResponse(
            responseCode = "201",
            description = "User registered successfully"
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        AuthResponse response =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Login user",
            description = "Authenticates user and returns JWT token"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Login successful"
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        AuthResponse response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get current logged-in user",
            description = "Returns details of the authenticated user"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Current user retrieved successfully"
    )
    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> me(
            Authentication authentication
    ) {

        CurrentUserResponse currentUser =
                authService.getCurrentLoggedInUser(
                        authentication
                );

        return ResponseEntity.ok(currentUser);
    }


    @GetMapping("/health")
    public String getHealth() {
        return "Running";
    }
}
