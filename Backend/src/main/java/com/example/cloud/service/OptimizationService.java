package com.example.cloud.service;

import com.example.cloud.dto.OptimizationResponse;
import com.example.cloud.enums.ResourceType;

import java.util.UUID;

public interface OptimizationService {

    OptimizationResponse analyzeResource(
            UUID cloudAccountId,
            String resourceId,
            ResourceType resourceType,
            String email
    );
}