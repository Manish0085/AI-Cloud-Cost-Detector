package com.example.cloud.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(
        name = "OptimizationReportDetailsResponse",
        description = "Detailed information of a saved optimization report"
)
public record OptimizationReportDetailsResponse(

        @Schema(
                description = "Unique report identifier",
                example = "e3dfd741-eb96-4bc1-bb31-05cf7a8b5946"
        )
        UUID id,

        @Schema(
                description = "Total resources analyzed",
                example = "12"
        )
        Integer totalResources,

        @Schema(
                description = "Total optimization findings generated",
                example = "25"
        )
        Integer totalFindings,

        @Schema(
                description = "AI-generated executive summary",
                example = "Analysis reveals several underutilized EC2 instances and idle resources that can be optimized to reduce cloud expenditure."
        )
        String executiveSummary,

        @Schema(
                description = "Report creation timestamp",
                example = "2026-06-09T10:43:25"
        )
        LocalDateTime createdAt

) implements Serializable {
}