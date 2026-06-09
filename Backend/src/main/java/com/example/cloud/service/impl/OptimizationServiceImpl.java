package com.example.cloud.service.impl;

import com.example.cloud.dto.*;
import com.example.cloud.service.AwsDiscoveryService;
import com.example.cloud.service.OptimizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OptimizationServiceImpl
        implements OptimizationService {

    private final AwsDiscoveryService awsDiscoveryService;

    @Override
    public OptimizationResponse analyzeEc2(

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

        List<Recommendation> recommendations =
                new ArrayList<>();

        if (metrics.cpuUtilization() < 5) {

            recommendations.add(

                    new Recommendation(

                            "COST",

                            "Downsize instance",

                            "CPU utilization remained below 5%"
                    )
            );
        }

        if (metrics.networkIn() < 1000
                && metrics.networkOut() < 1000) {

            recommendations.add(

                    new Recommendation(

                            "COST",

                            "Investigate idle workload",

                            "Network activity is extremely low"
                    )
            );
        }

        if (metrics.diskReadBytes() == 0
                && metrics.diskWriteBytes() == 0) {

            recommendations.add(

                    new Recommendation(

                            "PERFORMANCE",

                            "Review necessity of instance",

                            "No disk activity detected"
                    )
            );
        }

        if (recommendations.isEmpty()) {

            recommendations.add(

                    new Recommendation(

                            "INFO",

                            "No optimization opportunities detected",

                            "Resource appears healthy"
                    )
            );
        }

        return new OptimizationResponse(

                resourceId,

                details.instanceType(),

                recommendations,

                "$3-$10/month"
        );
    }
}