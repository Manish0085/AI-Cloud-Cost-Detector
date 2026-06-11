package com.example.cloud.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "CurrentUserResponse",
        description = "Details of the currently authenticated user"
)
public record CurrentUserResponse(

        @Schema(
                description = "User's full name",
                example = "Manish Kumar"
        )
        String name,

        @Schema(
                description = "User's email address",
                example = "manish@gmail.com"
        )
        String email,

        @Schema(
                description = "User role",
                example = "USER"
        )
        String role

) {
}