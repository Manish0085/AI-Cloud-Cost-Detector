package com.example.cloud.service;

import com.example.cloud.dto.AccountOptimizationResponse;
import com.example.cloud.dto.OptimizationReportResponse;
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
}