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

        double estimatedMonthlyCost =
                pricingService.getEc2HourlyPrice(
                        details.instanceType(),
                        region
                ) * 24 * 30;

        List<ResourceFinding> findings =
                new ArrayList<>();

        String metricsSummary = """
            CPU Utilization: %.2f%%
            Network In: %.2f bytes
            Network Out: %.2f bytes
            Disk Read Bytes: %.2f
            Disk Write Bytes: %.2f
            """.formatted(
                metrics.cpuUtilization(),
                metrics.networkIn(),
                metrics.networkOut(),
                metrics.diskReadBytes(),
                metrics.diskWriteBytes()
        );

        // High CPU workload
        if (metrics.cpuUtilization() > 80) {

            findings.add(
                    new ResourceFinding(
                            resourceId,
                            "EC2",
                            "High CPU Utilization",
                            metricsSummary + """

                        Observation:
                        CPU utilization is critically high and indicates an actively used workload.
                        """,
                            0.0
                    )
            );
        }

        // Low CPU
        if (metrics.cpuUtilization() < 5) {

            findings.add(
                    new ResourceFinding(
                            resourceId,
                            "EC2",
                            "Downsize Instance",
                            metricsSummary + """

                        Observation:
                        CPU utilization remained below 5%%.
                        Consider rightsizing the instance.
                        """,
                            estimatedMonthlyCost * 0.30
                    )
            );
        }

        // Low network activity
        if (metrics.networkIn() < 1000
                && metrics.networkOut() < 1000) {

            findings.add(
                    new ResourceFinding(
                            resourceId,
                            "EC2",
                            "Investigate Idle Workload",
                            metricsSummary + """

                        Observation:
                        Network activity is extremely low.
                        """,
                            estimatedMonthlyCost * 0.50
                    )
            );
        }

        // Disk inactivity
        if (metrics.diskReadBytes() == 0
                && metrics.diskWriteBytes() == 0) {

            findings.add(
                    new ResourceFinding(
                            resourceId,
                            "EC2",
                            "Review Disk Activity",
                            metricsSummary + """

                        Observation:
                        No disk activity detected.

                        Note:
                        Disk inactivity alone does not indicate an idle resource.
                        """,
                            0.0
                    )
            );
        }

        // Strong idle signal
        if (metrics.cpuUtilization() < 5
                && metrics.networkIn() < 1000
                && metrics.networkOut() < 1000
                && metrics.diskReadBytes() == 0
                && metrics.diskWriteBytes() == 0) {

            findings.add(
                    new ResourceFinding(
                            resourceId,
                            "EC2",
                            "Potentially Idle Resource",
                            metricsSummary + """

                        Observation:
                        Multiple utilization indicators suggest the instance may be idle.
                        """,
                            estimatedMonthlyCost
                    )
            );
        }

        if (findings.isEmpty()) {

            findings.add(
                    new ResourceFinding(
                            resourceId,
                            "EC2",
                            "No Optimization Needed",
                            metricsSummary + """

                        Observation:
                        Resource utilization appears healthy.
                        """,
                            0.0
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

        String bucketSummary = """
        Bucket Name: %s
        Versioning Enabled: %s
        Public Access Blocked: %s
        """.formatted(
                bucketName,
                details.versioningEnabled(),
                details.publicAccessBlocked()
        );

        if (!details.versioningEnabled()) {

            findings.add(
                    new ResourceFinding(
                            bucketName,
                            "S3",
                            "Enable Versioning",
                            bucketSummary + """

                    Observation:
                    Bucket versioning is disabled.

                    Risk:
                    Accidental object deletion or overwrite may result in data loss.
                    """,
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
                            bucketSummary + """

                    Observation:
                    Public access is not fully blocked.

                    Risk:
                    Sensitive data may be unintentionally exposed to the internet.
                    """,
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
                            bucketSummary + """

                    Observation:
                    The bucket follows recommended AWS security and governance practices.
                    """,
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

        String rdsSummary = """
        Database Identifier: %s
        Status: %s
        Allocated Storage: %d GB
        Engine: %s
        Instance Class: %s
        """.formatted(
                dbIdentifier,
                details.status(),
                details.allocatedStorage(),
                details.engine(),
                details.instanceClass()
        );

        if (!"available".equalsIgnoreCase(details.status())) {

            findings.add(
                    new ResourceFinding(
                            dbIdentifier,
                            "RDS",
                            "Investigate Database Health",
                            rdsSummary + """

                    Observation:
                    Database is not in AVAILABLE state.

                    Risk:
                    Database availability or connectivity may be impacted.
                    """,
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
                            rdsSummary + """

                    Observation:
                    Allocated storage is relatively small.

                    Risk:
                    Future workload growth may result in storage exhaustion and application downtime.
                    """,
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
                            rdsSummary + """

                    Observation:
                    Database appears healthy and follows expected operational standards.
                    """,
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

        String eksSummary = """
        Cluster Name: %s
        Status: %s
        Node Count: %d
        Kubernetes Version: %s
        Endpoint Public Access: %s
        """.formatted(
                clusterName,
                details.status(),
                details.nodeCount(),
                details.version(),
                details.endpoint()
        );
        if (!"ACTIVE".equalsIgnoreCase(details.status())) {
            findings.add(
                    new ResourceFinding(
                            clusterName,
                            "EKS",
                            "Investigate Cluster Health",
                            eksSummary + """
                    Observation:
                    Cluster is not in ACTIVE state.

                    Risk:
                    Kubernetes workloads may experience scheduling,
                    networking, or availability issues.
                    """,
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
                            eksSummary + """

                    Observation:
                    No worker nodes or node groups are attached to the cluster.

                    Risk:
                    Applications cannot be scheduled and the cluster
                    may be generating unnecessary control-plane costs.
                    """,
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
                            eksSummary + """

                    Observation:
                    Cluster appears healthy and operational.

                    Risk:
                    No immediate optimization or operational concerns detected.
                    """,
                            0.0
                    )
            );
        }

        return findings;
    }

}

