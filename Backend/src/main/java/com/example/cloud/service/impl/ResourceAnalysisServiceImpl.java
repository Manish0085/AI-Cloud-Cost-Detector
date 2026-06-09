package com.example.cloud.service.impl;

import com.example.cloud.dto.Ec2DetailsResponse;
import com.example.cloud.dto.ResourceFinding;
import com.example.cloud.dto.ResourceMetricsResponse;
import com.example.cloud.service.AwsDiscoveryService;
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

    @Override
    public List<ResourceFinding> analyzeResource(

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

        List<ResourceFinding> findings =
                new ArrayList<>();

        if (metrics.cpuUtilization() < 5) {

            findings.add(

                    new ResourceFinding(

                            resourceId,

                            "EC2",

                            "Downsize Instance",

                            "CPU utilization remained below 5%"
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

                            "Network activity is extremely low"
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

                            "No disk activity detected"
                    )
            );
        }

        if (findings.isEmpty()) {

            findings.add(

                    new ResourceFinding(

                            resourceId,

                            "EC2",

                            "No Optimization Needed",

                            "Resource appears healthy"
                    )
            );
        }

        return findings;
    }
}