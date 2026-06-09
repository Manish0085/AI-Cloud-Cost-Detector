package com.example.cloud.dto;

public record ResourceFinding(

        String resourceId,

        String resourceType,

        String recommendation,

        String reason
) {
}