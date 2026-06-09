package com.example.cloud.dto;

import java.util.UUID;

public record CloudAccountResponse(

        UUID id,

        String accountName,

        String provider,

        boolean connected
) {
}