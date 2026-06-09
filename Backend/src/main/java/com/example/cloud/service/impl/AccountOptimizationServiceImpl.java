package com.example.cloud.service.impl;

import com.example.cloud.dto.AccountOptimizationResponse;
import com.example.cloud.dto.ResourceFinding;
import com.example.cloud.dto.ResourceResponse;
import com.example.cloud.enums.ResourceType;
import com.example.cloud.service.AccountOptimizationService;
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

        List<ResourceFinding> findings =
                new ArrayList<>();

        for (ResourceResponse resource : resources) {

            findings.addAll(

                    resourceAnalysisService.analyzeResource(

                            cloudAccountId,

                            resource.resourceId(),

                            email
                    )
            );
        }

        String aiRecommendation =
                "AI analysis pending";

        return new AccountOptimizationResponse(

                resources.size(),

                findings,

                aiRecommendation
        );
    }
}