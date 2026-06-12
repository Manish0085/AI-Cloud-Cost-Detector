package com.example.cloud.dto;

import java.io.Serializable;

public record ResourceResponse(

        String resourceId,

        String resourceName,

        String resourceType,

        String region,

        String status

) implements Serializable {
}