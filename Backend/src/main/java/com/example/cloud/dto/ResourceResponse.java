package com.example.cloud.dto;

public record ResourceResponse(

        String resourceId,

        String resourceName,

        String resourceType,

        String region,

        String status

) {
}