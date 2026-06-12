package com.example.cloud.service.impl;

import com.example.cloud.dto.*;
import com.example.cloud.enums.ResourceType;
import com.example.cloud.service.AwsDiscoveryService;
import com.example.cloud.service.PricingService;
import com.example.cloud.service.ResourceAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceAnalysisServiceImpl
        implements ResourceAnalysisService {

    private final AwsDiscoveryService awsDiscoveryService;
    private final PricingService pricingService;

    @Override
    public List<ResourceFinding> analyzeResource(

            UUID cloudAccountId,

            String resourceId,

            ResourceType resourceType,

            String email
    ) {

        return switch (resourceType) {

            case EC2 ->
                    analyzeEc2(
                            cloudAccountId,
                            resourceId,
                            email
                    );

            case S3 ->
                    analyzeS3(
                            cloudAccountId,
                            resourceId,
                            email
                    );

            case RDS ->
                    analyzeRds(
                            cloudAccountId,
                            resourceId,
                            email
                    );

            case EKS ->
                    analyzeEks(
                            cloudAccountId,
                            resourceId,
                            email
                    );

            default ->
                    List.of();
        };
    }


    private List<ResourceFinding> analyzeEc2(

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

        ResourceMetricsResponse metrics =
                awsDiscoveryService.getMetrics(
                        cloudAccountId,
                        resourceId,
                        email
                );

        String region =
                details.availabilityZone()
                        .substring(
                                0,
                                details.availabilityZone().length() - 1
                        );

        double estimatedMonthlySavings =
                pricingService.getEc2HourlyPrice(
                        details.instanceType(),
                        region
                ) * 24 * 30;

        List<ResourceFinding> findings =
                new ArrayList<>();

        if (metrics.cpuUtilization() < 5) {

            findings.add(
                    new ResourceFinding(
                            resourceId,
                            "EC2",
                            "Downsize Instance",
                            "CPU utilization remained below 5%",
                            estimatedMonthlySavings * 0.0
                    )
            );
        }

        if (metrics.networkIn() < 1000
                && metrics.networkOut() < 1000) {

            findings.add(
                    new ResourceFinding(
                            resourceId,
                            "EC2",
                            "Investigate Idle Workload",
                            "Network activity is extremely low",
                            estimatedMonthlySavings * 0.0
                    )
            );
        }

        if (metrics.diskReadBytes() == 0
                && metrics.diskWriteBytes() == 0) {

            findings.add(
                    new ResourceFinding(
                            resourceId,
                            "EC2",
                            "Review Necessity Of Resource",
                            "No disk activity detected",
                            estimatedMonthlySavings * 0.0
                    )
            );
        }

        if (findings.isEmpty()) {

            findings.add(
                    new ResourceFinding(
                            resourceId,
                            "EC2",
                            "No Optimization Needed",
                            "Resource appears healthy",
                            estimatedMonthlySavings * 0.0
                    )
            );
        }

        return findings;
    }

    private List<ResourceFinding> analyzeS3(
            UUID cloudAccountId,
            String bucketName,
            String email
    ) {

        S3DetailsResponse details =
                awsDiscoveryService.getS3Details(
                        cloudAccountId,
                        bucketName,
                        email
                );

        List<ResourceFinding> findings =
                new ArrayList<>();

        if (!details.versioningEnabled()) {

            findings.add(

                    new ResourceFinding(
                            bucketName,
                            "S3",
                            "Enable Versioning",
                            "Bucket versioning is disabled",
                            0.0
                    )
            );
        }

        if (!details.publicAccessBlocked()) {

            findings.add(
                    new ResourceFinding(
                            bucketName,
                            "S3",
                            "Block Public Access",
                            "Bucket is publicly accessible",
                            0.0
                    )
            );
        }

        if (findings.isEmpty()) {

            findings.add(

                    new ResourceFinding(
                            bucketName,
                            "S3",
                            "No Optimization Needed",
                            "Bucket follows recommended practices",
                            0.0
                    )
            );
        }

        return findings;
    }


    private List<ResourceFinding> analyzeRds(

            UUID cloudAccountId,

            String dbIdentifier,

            String email
    ) {

        RdsDetailsResponse details =
                awsDiscoveryService.getRdsDetails(
                        cloudAccountId,
                        dbIdentifier,
                        email
                );

        List<ResourceFinding> findings =
                new ArrayList<>();

        if (!"available".equalsIgnoreCase(
                details.status()
        )) {
            findings.add(
                    new ResourceFinding(
                            dbIdentifier,
                            "RDS",
                            "Investigate Database Health",
                            "Database is not in AVAILABLE state",
                            0.0
                    )
            );
        }

        if (details.allocatedStorage() < 20) {
            findings.add(
                    new ResourceFinding(
                            dbIdentifier,
                            "RDS",
                            "Review Storage Allocation",
                            "Allocated storage is very small",
                            0.0
                    )
            );
        }

        if (findings.isEmpty()) {
            findings.add(
                    new ResourceFinding(
                            dbIdentifier,
                            "RDS",
                            "No Optimization Needed",
                            "Database appears healthy",
                            0.0
                    )
            );
        }

        return findings;
    }


    private List<ResourceFinding> analyzeEks(
            UUID cloudAccountId,
            String clusterName,
            String email
    ) {
        EksDetailsResponse details =
                awsDiscoveryService.getEksDetails(
                        cloudAccountId,
                        clusterName,
                        email
                );
        List<ResourceFinding> findings =
                new ArrayList<>();

        if (!"ACTIVE".equalsIgnoreCase(
                details.status()
        )) {
            findings.add(
                    new ResourceFinding(
                            clusterName,
                            "EKS",
                            "Investigate Cluster Health",
                            "Cluster is not active",
                            0.0
                    )
            );
        }

        if (details.nodeCount() == 0) {
            findings.add(
                    new ResourceFinding(
                            clusterName,
                            "EKS",
                            "Cluster Has No Worker Nodes",
                            "No node groups found",
                            0.0
                    )
            );
        }

        if (findings.isEmpty()) {
            findings.add(
                    new ResourceFinding(
                            clusterName,
                            "EKS",
                            "No Optimization Needed",
                            "Cluster appears healthy",
                            0.0
                    )
            );
        }

        return findings;
    }
}

