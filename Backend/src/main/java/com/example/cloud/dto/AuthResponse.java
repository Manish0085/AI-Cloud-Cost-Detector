package com.example.cloud.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "AuthResponse",
        description = "Response returned after successful authentication"
)
public record AuthResponse(

        @Schema(
                description = "JWT access token used for authenticated requests",
                example = "eyJhbGciOiJIUzI1NiJ9..."
        )
        String token,

        @Schema(
                description = "Authentication result message",
                example = "Login successful"
        )
        String message

) {
}