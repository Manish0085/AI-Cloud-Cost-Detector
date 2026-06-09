package com.example.cloud.dto;

import java.util.List;

public record ResourceOptimizationResult(

        String resourceId,

        String resourceName,

        String resourceType,

        List<ResourceFinding> findings,

        String aiRecommendation
) {
}