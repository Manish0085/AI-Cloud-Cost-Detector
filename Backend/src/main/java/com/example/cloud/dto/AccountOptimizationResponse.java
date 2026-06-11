package com.example.cloud.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(
        name = "AccountOptimizationResponse",
        description = "Result of cloud account optimization analysis"
)
public record AccountOptimizationResponse(

        @Schema(
                description = "Total resources analyzed",
                example = "10"
        )
        int totalResources,

        @Schema(
                description = "Total optimization findings discovered",
                example = "18"
        )
        int totalFindings,

        @Schema(
                description = "Resource-wise optimization recommendations"
        )
        List<ResourceOptimizationResult> resources,

        @Schema(
                description = "AI-generated executive summary of the optimization report",
                example = "Several EC2 instances are significantly underutilized and can be downsized to reduce monthly cloud costs."
        )
        String executiveSummary

) {
}