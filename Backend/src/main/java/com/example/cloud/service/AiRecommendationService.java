package com.example.cloud.service;

import com.example.cloud.dto.*;

import java.util.List;

public interface AiRecommendationService {

    String generateResourceRecommendation(

            String resourceName,

            List<ResourceFinding> findings
    );

    String generateExecutiveSummary(

            List<ResourceOptimizationResult> resources
    );
}