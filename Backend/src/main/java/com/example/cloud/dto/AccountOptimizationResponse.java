package com.example.cloud.dto;

import java.util.List;

public record AccountOptimizationResponse(

        int totalResources,

        List<ResourceFinding> findings,

        String aiRecommendation
) {
}