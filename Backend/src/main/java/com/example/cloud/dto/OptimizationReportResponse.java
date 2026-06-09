package com.example.cloud.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record OptimizationReportResponse(

        UUID id,

        Integer totalResources,

        Integer totalFindings,

        LocalDateTime createdAt
) {
}