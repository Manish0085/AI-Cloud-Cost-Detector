package com.example.cloud.dto;

public record CurrentUserResponse(
        String name,
        String email,
        String role
) {}