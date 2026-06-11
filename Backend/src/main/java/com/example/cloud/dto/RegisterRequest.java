package com.example.cloud.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        name = "RegisterRequest",
        description = "Request payload for user registration"
)
public record RegisterRequest(

        @Schema(
                description = "Full name of the user",
                example = "Manish Kumar"
        )
        @NotBlank(message = "Name is required")
        String name,

        @Schema(
                description = "User email address",
                example = "manish@gmail.com"
        )
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @Schema(
                description = "User password",
                example = "Password@123",
                minLength = 8
        )
        @NotBlank(message = "Password is required")
        @Size(
                min = 8,
                message = "Password must be at least 8 characters"
        )
        String password

) {
}