package com.example.cloud.controller;

import com.example.cloud.dto.*;
import com.example.cloud.enums.ResourceType;
import com.example.cloud.service.AccountOptimizationService;
import com.example.cloud.service.AwsDiscoveryService;
import com.example.cloud.service.CloudAccountService;
import com.example.cloud.service.OptimizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v1/cloud")
@RequiredArgsConstructor
@Tag(
        name = "Cloud Optimization",
        description = "Cloud account management, resource discovery, optimization and reporting APIs"
)
public class CloudAccountController {

    private final CloudAccountService cloudAccountService;
    private final AwsDiscoveryService awsDiscoveryService;
    private final OptimizationService optimizationService;
    private final AccountOptimizationService accountOptimizationService;

    @Operation(
            summary = "Connect AWS Account",
            description = "Connects an AWS account using access key and secret key"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Cloud account connected successfully"
    )
    @PostMapping("/connect")
    public ResponseEntity<String> connectAccount(
            @Valid @RequestBody ConnectCloudRequest request,
            Authentication authentication
    ) {

        cloudAccountService.connectCloudAccount(
                request,
                authentication.getName()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Cloud account connected successfully");
    }

    @Operation(
            summary = "Discover Resources",
            description = "Discovers resources available in a cloud account"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Resources retrieved successfully"
    )
    @GetMapping("/{id}/resources")
    public ResponseEntity<List<ResourceResponse>> getResources(

            @PathVariable UUID id,

            @RequestParam(defaultValue = "EC2")
            ResourceType type,

            Authentication authentication
    ) {

        return ResponseEntity.ok(
                awsDiscoveryService.discoverResources(
                        id,
                        type,
                        authentication.getName()
                )
        );
    }

    @Operation(
            summary = "Get Connected Accounts",
            description = "Returns all cloud accounts connected by the current user"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Accounts retrieved successfully"
    )
    @GetMapping("/accounts")
    public ResponseEntity<List<CloudAccountResponse>>
    getAccounts(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                cloudAccountService.getAccounts(
                        authentication.getName()
                )
        );
    }

    @Operation(
            summary = "Get Resource Details",
            description = "Returns detailed information for a specific EC2 resource"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Resource details retrieved successfully"
    )
    @GetMapping("/{accountId}/resources/{resourceId}")
    public ResponseEntity<Ec2DetailsResponse> getResourceDetails(

            @PathVariable UUID accountId,

            @NotBlank(message = "Resource id is required")
            @PathVariable String resourceId,

            Authentication authentication
    ) {

        return ResponseEntity.ok(
                awsDiscoveryService.getEc2Details(
                        accountId,
                        resourceId,
                        authentication.getName()
                )
        );
    }

    @Operation(
            summary = "Get Resource Metrics",
            description = "Returns CloudWatch metrics for a resource"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Metrics retrieved successfully"
    )
    @GetMapping("/{accountId}/resources/{resourceId}/metrics")
    public ResponseEntity<ResourceMetricsResponse> getMetrics(

            @PathVariable UUID accountId,

            @PathVariable String resourceId,

            Authentication authentication
    ) {

        return ResponseEntity.ok(
                awsDiscoveryService.getMetrics(
                        accountId,
                        resourceId,
                        authentication.getName()
                )
        );
    }

    @Operation(
            summary = "Optimize Resource",
            description = "Analyzes a single EC2 resource and generates optimization recommendations"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Optimization completed successfully"
    )
    @PostMapping(
            "/{accountId}/resources/{resourceId}/optimize"
    )
    public ResponseEntity<OptimizationResponse>
    optimize(
            @PathVariable UUID accountId,
            @PathVariable String resourceId,
            @RequestParam ResourceType type,
            Authentication authentication
    ) {
        return ResponseEntity.ok(

                optimizationService.analyzeResource(
                        accountId,
                        resourceId,
                        type,
                        authentication.getName()
                )
        );
    }

    @Operation(
            summary = "Optimize Cloud Account",
            description = "Runs AI-powered optimization analysis across all resources in a cloud account using AWS metrics, RAG and OpenRouter"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Account optimization completed successfully"
    )
    @PostMapping("/{accountId}/optimize")
    public ResponseEntity<AccountOptimizationResponse>
    optimizeAccount(
            @PathVariable UUID accountId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                accountOptimizationService.optimizeAccount(
                        accountId,
                        authentication.getName()
                )
        );
    }

    @Operation(
            summary = "Get Optimization Reports",
            description = "Returns paginated optimization reports for a cloud account"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Reports retrieved successfully"
    )
    @GetMapping("/{accountId}/reports")
    public ResponseEntity<Page<OptimizationReportResponse>>
    getReports(

            @PathVariable UUID accountId,
            Authentication authentication,
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "10")
            int size
    ) {

        return ResponseEntity.ok(
                accountOptimizationService.getReports(
                        accountId,
                        authentication.getName(),
                        page,
                        size
                )
        );
    }

    @Operation(
            summary = "Get Report Details",
            description = "Returns details of a specific optimization report"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Report retrieved successfully"
    )
    @GetMapping("/reports/{reportId}")
    public ResponseEntity<OptimizationReportDetailsResponse> getReport(
            @PathVariable UUID reportId
    ) {

        return ResponseEntity.ok(
                accountOptimizationService.getReport(reportId)
        );
    }




    @Operation(
            summary = "Optimize Resource Type",
            description = "Runs optimization for a specific resource type such as EC2, S3, RDS or EKS"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Optimization completed successfully"
    )
    @PostMapping("/{accountId}/optimize/{resourceType}")
    public ResponseEntity<AccountOptimizationResponse>
    optimizeResourceType(
            @PathVariable UUID accountId,
            @PathVariable ResourceType resourceType,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                accountOptimizationService.optimizeResourceType(
                        accountId,
                        resourceType,
                        authentication.getName()
                )
        );
    }


    @GetMapping("/{accountId}/summary")
    public ResponseEntity<DashboardSummaryResponse>
    getSummary(

            @PathVariable UUID accountId,

            Authentication authentication
    ) {

        return ResponseEntity.ok(
                accountOptimizationService.getSummary(
                        accountId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{accountId}/dashboard")
    public ResponseEntity<DashboardResponse>
    getDashboard(

            @PathVariable UUID accountId,

            Authentication authentication
    ) {

        return ResponseEntity.ok(
                accountOptimizationService.getDashboard(
                        accountId,
                        authentication.getName()
                )
        );
    }


    @GetMapping("/{accountId}/s3/{bucketName}")
    public ResponseEntity<S3DetailsResponse>
    getS3Details(

            @PathVariable UUID accountId,

            @PathVariable String bucketName,

            Authentication authentication
    ) {

        return ResponseEntity.ok(

                awsDiscoveryService.getS3Details(

                        accountId,

                        bucketName,

                        authentication.getName()
                )
        );
    }


    @GetMapping("/{accountId}/eks/{clusterName}")
    public ResponseEntity<EksDetailsResponse>
    getEksDetails(

            @PathVariable UUID accountId,

            @PathVariable String clusterName,

            Authentication authentication
    ) {

        return ResponseEntity.ok(

                awsDiscoveryService.getEksDetails(

                        accountId,

                        clusterName,

                        authentication.getName()
                )
        );
    }


}