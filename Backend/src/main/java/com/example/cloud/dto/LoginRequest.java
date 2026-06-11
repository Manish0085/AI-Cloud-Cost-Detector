package com.example.cloud.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(
        name = "LoginRequest",
        description = "Request payload for user login"
)
public record LoginRequest(

        @Schema(
                description = "Registered email address",
                example = "manish@gmail.com"
        )
        @NotBlank(message = "Email is required")
        @Email(message = "Please enter a valid email address")
        String email,

        @Schema(
                description = "Account password",
                example = "Password@123"
        )
        @NotBlank(message = "Password is required")
        String password

) {
}