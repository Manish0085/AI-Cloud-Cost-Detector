package com.example.cloud.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record OptimizationReportDetailsResponse(

        UUID id,

        Integer totalResources,

        Integer totalFindings,

        String executiveSummary,

        LocalDateTime createdAt
) {
}