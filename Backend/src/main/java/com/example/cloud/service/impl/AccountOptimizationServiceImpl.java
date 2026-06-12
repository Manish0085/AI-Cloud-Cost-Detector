package com.example.cloud.service.impl;

import com.example.cloud.dto.*;
import com.example.cloud.entity.CloudAccount;
import com.example.cloud.entity.OptimizationReport;
import com.example.cloud.enums.ResourceType;
import com.example.cloud.exception.CloudAccountNotFoundException;
import com.example.cloud.exception.ResourceNotFoundException;
import com.example.cloud.respository.CloudAccountRepository;
import com.example.cloud.respository.OptimizationReportRepository;
import com.example.cloud.service.AccountOptimizationService;
import com.example.cloud.service.AiRecommendationService;
import com.example.cloud.service.AwsDiscoveryService;
import com.example.cloud.service.ResourceAnalysisService;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
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
                            ResourceType.valueOf(resource.resourceType()),
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


    @Cacheable(
            value = "reports",
            key = "#cloudAccountId + '_' + #page + '_' + #size"
    )
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


    @Cacheable(
            value = "report-details",
            key = "#reportId"
    )
    @Override
    public OptimizationReportDetailsResponse
    getReport(UUID reportId) {

        OptimizationReport report =
                optimizationReportRepository
                        .findById(reportId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Report not found"
                                )
                        );

        return new OptimizationReportDetailsResponse(
                report.getId(),
                report.getTotalResources(),
                report.getTotalFindings(),
                report.getExecutiveSummary(),
                report.getCreatedAt()
        );
    }


    @Override
    public AccountOptimizationResponse optimizeResourceType(
            UUID cloudAccountId,
            ResourceType resourceType,
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
                        resourceType,
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
                            ResourceType.valueOf(resource.resourceType()),
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
                                r -> r.findings().size()
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

    @Override
    public DashboardSummaryResponse getSummary(
            UUID accountId,
            String email
    ) {

        List<ResourceResponse> resources =
                awsDiscoveryService.discoverResources(
                        accountId,
                        ResourceType.ALL,
                        email
                );

        int ec2Count =
                (int) resources.stream()
                        .filter(r ->
                                r.resourceType()
                                        .equals("EC2"))
                        .count();

        int s3Count =
                (int) resources.stream()
                        .filter(r ->
                                r.resourceType()
                                        .equals("S3"))
                        .count();

        int rdsCount =
                (int) resources.stream()
                        .filter(r ->
                                r.resourceType()
                                        .equals("RDS"))
                        .count();

        int eksCount =
                (int) resources.stream()
                        .filter(r ->
                                r.resourceType()
                                        .equals("EKS"))
                        .count();

        int totalFindings =
                optimizationReportRepository
                        .findByCloudAccountId(accountId)
                        .stream()
                        .mapToInt(
                                OptimizationReport::getTotalFindings
                        )
                        .sum();

        return new DashboardSummaryResponse(
                resources.size(),
                ec2Count,
                s3Count,
                rdsCount,
                eksCount,
                totalFindings
        );
    }


    @Override
    public DashboardResponse getDashboard(
            UUID accountId,
            String email
    ) {

        DashboardSummaryResponse summary =
                getSummary(
                        accountId,
                        email
                );

        Pageable pageable =
                PageRequest.of(
                        0,
                        5
                );

        List<OptimizationReportResponse>
                reports =
                optimizationReportRepository
                        .findByCloudAccountIdOrderByCreatedAtDesc(
                                accountId,
                                pageable
                        )
                        .stream()
                        .map(report ->
                                new OptimizationReportResponse(
                                        report.getId(),
                                        report.getTotalResources(),
                                        report.getTotalFindings(),
                                        report.getCreatedAt()
                                )
                        )
                        .toList();

        return new DashboardResponse(
                summary,
                reports
        );
    }
}