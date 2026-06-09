package com.example.cloud.service.impl;

import com.example.cloud.dto.Ec2DetailsResponse;
import com.example.cloud.dto.OptimizationResponse;
import com.example.cloud.dto.ResourceFinding;
import com.example.cloud.service.AiRecommendationService;
import com.example.cloud.service.AwsDiscoveryService;
import com.example.cloud.service.OptimizationService;
import com.example.cloud.service.ResourceAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OptimizationServiceImpl
        implements OptimizationService {

    private final AwsDiscoveryService awsDiscoveryService;

    private final ResourceAnalysisService resourceAnalysisService;

    private final AiRecommendationService aiRecommendationService;

    @Override
    public OptimizationResponse analyzeEc2(

            UUID cloudAccountId,

            String resourceId,

            String email
    ) {

        Ec2DetailsResponse details =
                awsDiscoveryService.getEc2Details(
                        cloudAccountId,
                        resourceId,
                        email
                );

        List<ResourceFinding> findings =
                resourceAnalysisService.analyzeResource(

                        cloudAccountId,

                        resourceId,

                        email
                );

        String aiRecommendation =
                aiRecommendationService
                        .generateResourceRecommendation(

                                details.instanceName(),

                                findings
                        );

        return new OptimizationResponse(

                resourceId,

                details.instanceType(),

                aiRecommendation
        );
    }
}