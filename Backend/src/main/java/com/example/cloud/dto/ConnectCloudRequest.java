package com.example.cloud.dto;

import com.example.cloud.enums.CloudProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(
        name = "ConnectCloudRequest",
        description = "Request payload for connecting a cloud account"
)
public record ConnectCloudRequest(

        @Schema(
                description = "Display name of the cloud account",
                example = "My AWS Account"
        )
        @NotBlank(message = "Account name is required")
        @Size(
                min = 3,
                max = 50,
                message = "Account name must be between 3 and 50 characters"
        )
        String accountName,

        @Schema(
                description = "Cloud provider",
                example = "AWS",
                allowableValues = {
                        "AWS"
                }
        )
        @NotNull(message = "Cloud provider is required")
        CloudProvider provider,

        @Schema(
                description = "AWS Access Key",
                example = "AKIAIOSFODNN7EXAMPLE"
        )
        @NotBlank(message = "Access key is required")
        String accessKey,

        @Schema(
                description = "AWS Secret Access Key",
                example = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
        )
        @NotBlank(message = "Secret key is required")
        String secretKey

) {
}