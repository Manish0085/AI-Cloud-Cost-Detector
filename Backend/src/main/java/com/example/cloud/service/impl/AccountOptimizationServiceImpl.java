package com.example.cloud.service.impl;

import com.example.cloud.dto.AccountOptimizationResponse;
import com.example.cloud.dto.ResourceFinding;
import com.example.cloud.dto.ResourceOptimizationResult;
import com.example.cloud.dto.ResourceResponse;
import com.example.cloud.enums.ResourceType;
import com.example.cloud.service.AccountOptimizationService;
import com.example.cloud.service.AiRecommendationService;
import com.example.cloud.service.AwsDiscoveryService;
import com.example.cloud.service.ResourceAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountOptimizationServiceImpl
        implements AccountOptimizationService {

    private final AwsDiscoveryService awsDiscoveryService;

    private final ResourceAnalysisService resourceAnalysisService;

    private final AiRecommendationService aiRecommendationService;

    @Override
    public AccountOptimizationResponse optimizeAccount(

            UUID cloudAccountId,

            String email
    ) {

        List<ResourceResponse> resources =
                awsDiscoveryService.discoverResources(
                        cloudAccountId,
                        ResourceType.ALL,
                        email
                );

        List<ResourceOptimizationResult> optimizedResources =
                new ArrayList<>();

        for (ResourceResponse resource : resources) {

            List<ResourceFinding> findings =
                    resourceAnalysisService.analyzeResource(

                            cloudAccountId,

                            resource.resourceId(),

                            email
                    );

            String aiRecommendation =
                    aiRecommendationService
                            .generateResourceRecommendation(

                                    resource.resourceName(),

                                    findings
                            );

            optimizedResources.add(

                    new ResourceOptimizationResult(

                            resource.resourceId(),

                            resource.resourceName(),

                            resource.resourceType(),

                            findings,

                            aiRecommendation
                    )
            );
        }

        int totalFindings =
                optimizedResources.stream()
                        .mapToInt(
                                resource ->
                                        resource.findings().size()
                        )
                        .sum();

        String executiveSummary =
                aiRecommendationService
                        .generateExecutiveSummary(
                                optimizedResources
                        );

        return new AccountOptimizationResponse(

                resources.size(),

                totalFindings,

                optimizedResources,

                executiveSummary
        );
    }
}