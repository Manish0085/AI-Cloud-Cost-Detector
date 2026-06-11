package com.example.cloud.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(
        name = "OptimizationReportResponse",
        description = "Summary information of a saved optimization report"
)
public record OptimizationReportResponse(

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
                description = "Report creation timestamp",
                example = "2026-06-09T10:43:25"
        )
        LocalDateTime createdAt

) {
}