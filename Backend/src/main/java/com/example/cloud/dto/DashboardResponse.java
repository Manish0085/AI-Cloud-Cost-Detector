package com.example.cloud.dto;

import java.util.List;

public record DashboardResponse(

        DashboardSummaryResponse summary,

        List<OptimizationReportResponse> recentReports
) {
}