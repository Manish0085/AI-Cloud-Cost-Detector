package com.example.cloud.controller;

import com.example.cloud.dto.*;
import com.example.cloud.enums.ResourceType;
import com.example.cloud.service.AccountOptimizationService;
import com.example.cloud.service.AwsDiscoveryService;
import com.example.cloud.service.CloudAccountService;
import com.example.cloud.service.OptimizationService;
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
public class CloudAccountController {

    private final CloudAccountService cloudAccountService;
    private final AwsDiscoveryService awsDiscoveryService;
    private final OptimizationService optimizationService;
    private final AccountOptimizationService accountOptimizationService;

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


    @PostMapping(
            "/{accountId}/resources/{resourceId}/optimize"
    )
    public ResponseEntity<OptimizationResponse>
    optimize(

            @PathVariable UUID accountId,

            @PathVariable String resourceId,

            Authentication authentication
    ) {

        return ResponseEntity.ok(

                optimizationService.analyzeEc2(

                        accountId,

                        resourceId,

                        authentication.getName()
                )
        );
    }


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
}