package com.example.cloud.service;

import com.example.cloud.dto.Ec2DetailsResponse;
import com.example.cloud.dto.ResourceMetricsResponse;
import com.example.cloud.dto.ResourceResponse;
import com.example.cloud.enums.ResourceType;

import java.util.List;
import java.util.UUID;

public interface AwsDiscoveryService {

    List<ResourceResponse> discoverResources(
            UUID cloudAccountId,
            ResourceType type,
            String email
    );

    public Ec2DetailsResponse getEc2Details(
            UUID cloudAccountId,
            String resourceId,
            String email
    );

    ResourceMetricsResponse getMetrics(
            UUID cloudAccountId,
            String resourceId,
            String email
    );


}