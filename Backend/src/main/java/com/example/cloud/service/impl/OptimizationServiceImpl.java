package com.example.cloud.service.impl;

import com.example.cloud.dto.Ec2DetailsResponse;
import com.example.cloud.dto.OptimizationResponse;
import com.example.cloud.dto.ResourceFinding;
import com.example.cloud.enums.ResourceType;
import com.example.cloud.service.*;
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
    public OptimizationResponse analyzeResource(

            UUID cloudAccountId,

            String resourceId,

            ResourceType resourceType,

            String email
    ) {

        List<ResourceFinding> findings =
                resourceAnalysisService.analyzeResource(
                        cloudAccountId,
                        resourceId,
                        resourceType,
                        email
                );

        String resourceName =
                getResourceName(
                        cloudAccountId,
                        resourceId,
                        resourceType,
                        email
                );

        String aiRecommendation =
                aiRecommendationService
                        .generateResourceRecommendation(
                                resourceName,
                                findings
                        );

        return new OptimizationResponse(
                resourceId,
                resourceType.name(),
                aiRecommendation
        );
    }

    private String getResourceName(

            UUID cloudAccountId,

            String resourceId,

            ResourceType resourceType,

            String email
    ) {

        return switch (resourceType) {

            case EC2 ->
                    awsDiscoveryService
                            .getEc2Details(
                                    cloudAccountId,
                                    resourceId,
                                    email
                            )
                            .instanceName();

            case S3 ->
                    awsDiscoveryService
                            .getS3Details(
                                    cloudAccountId,
                                    resourceId,
                                    email
                            )
                            .bucketName();

            case RDS ->
                    awsDiscoveryService
                            .getRdsDetails(
                                    cloudAccountId,
                                    resourceId,
                                    email
                            )
                            .dbIdentifier();

            case EKS ->
                    awsDiscoveryService
                            .getEksDetails(
                                    cloudAccountId,
                                    resourceId,
                                    email
                            )
                            .clusterName();

            default ->
                    resourceId;
        };
    }
}