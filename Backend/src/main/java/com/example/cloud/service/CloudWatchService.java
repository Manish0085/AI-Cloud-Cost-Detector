package com.example.cloud.service;

import com.example.cloud.dto.ResourceMetricsResponse;
import software.amazon.awssdk.regions.Region;

import java.util.UUID;

public interface CloudWatchService {

    ResourceMetricsResponse getMetrics(
            UUID cloudAccountId,
            String resourceId,
            String email
    );
}