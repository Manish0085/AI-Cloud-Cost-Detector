package com.example.cloud.service;

import com.example.cloud.dto.*;
import com.example.cloud.enums.ResourceType;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface AccountOptimizationService {

    AccountOptimizationResponse optimizeAccount(

            UUID cloudAccountId,

            String email
    );

    Page<OptimizationReportResponse> getReports(
            UUID cloudAccountId,
            String email,
            int page,
            int size
    );

    OptimizationReportDetailsResponse getReport(UUID reportId);


    AccountOptimizationResponse optimizeResourceType(
            UUID cloudAccountId,
            ResourceType resourceType,
            String email
    );

    DashboardSummaryResponse getSummary(
            UUID accountId,
            String email
    );

    DashboardResponse getDashboard(
            UUID accountId,
            String email
    );
}