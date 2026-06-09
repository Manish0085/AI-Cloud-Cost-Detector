package com.example.cloud.dto;

import java.util.List;

public record AccountOptimizationResponse(

        int totalResources,

        int totalFindings,

        List<ResourceOptimizationResult> resources,

        String executiveSummary
) {
}