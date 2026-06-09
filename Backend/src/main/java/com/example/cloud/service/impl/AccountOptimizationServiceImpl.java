package com.example.cloud.service.impl;

import com.example.cloud.dto.*;
import com.example.cloud.entity.CloudAccount;
import com.example.cloud.entity.OptimizationReport;
import com.example.cloud.enums.ResourceType;
import com.example.cloud.exception.CloudAccountNotFoundException;
import com.example.cloud.respository.CloudAccountRepository;
import com.example.cloud.respository.OptimizationReportRepository;
import com.example.cloud.service.AccountOptimizationService;
import com.example.cloud.service.AiRecommendationService;
import com.example.cloud.service.AwsDiscoveryService;
import com.example.cloud.service.ResourceAnalysisService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
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
    private final CloudAccountRepository cloudAccountRepository;
    private final OptimizationReportRepository
            optimizationReportRepository;

    @Override
    public AccountOptimizationResponse optimizeAccount(

            UUID cloudAccountId,

            String email
    ) {

        CloudAccount account =
                cloudAccountRepository.findById(
                        cloudAccountId
                ).orElseThrow(
                        () -> new CloudAccountNotFoundException(
                                "Cloud account not found"
                        )
                );

        List<ResourceResponse> resources =
                awsDiscoveryService.discoverResources(
                        cloudAccountId,
                        ResourceType.ALL,
                        email
                );

        List<ResourceOptimizationResult>
                optimizedResources =
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

        OptimizationReport report =
                OptimizationReport.builder()
                        .cloudAccountId(account.getId())
                        .totalResources(
                                resources.size()
                        )
                        .totalFindings(
                                totalFindings
                        )
                        .executiveSummary(
                                executiveSummary
                        )
                        .build();

        optimizationReportRepository.save(
                report
        );

        return new AccountOptimizationResponse(
                resources.size(),
                totalFindings,
                optimizedResources,
                executiveSummary
        );
    }


    @Override
    public Page<OptimizationReportResponse> getReports(
            UUID cloudAccountId,
            String email,
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );

        return optimizationReportRepository
                .findByCloudAccountIdOrderByCreatedAtDesc(
                        cloudAccountId,
                        pageable
                )
                .map(report ->
                        new OptimizationReportResponse(
                                report.getId(),
                                report.getTotalResources(),
                                report.getTotalFindings(),
                                report.getCreatedAt()
                        )
                );
    }
}