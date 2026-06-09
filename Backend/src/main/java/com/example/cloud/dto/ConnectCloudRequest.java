package com.example.cloud.dto;

import com.example.cloud.enums.CloudProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ConnectCloudRequest(

        @NotBlank(message = "Account name is required")
        @Size(min = 3, max = 50,
                message = "Account name must be between 3 and 50 characters")
        String accountName,

        @NotNull(message = "Cloud provider is required")
        CloudProvider provider,

        @NotBlank(message = "Access key is required")
        String accessKey,

        @NotBlank(message = "Secret key is required")
        String secretKey

) {
}