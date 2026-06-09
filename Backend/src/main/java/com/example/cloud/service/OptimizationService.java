package com.example.cloud.service;

import com.example.cloud.dto.OptimizationResponse;

import java.util.UUID;

public interface OptimizationService {

    OptimizationResponse analyzeEc2(

            UUID cloudAccountId,

            String resourceId,

            String email
    );
}