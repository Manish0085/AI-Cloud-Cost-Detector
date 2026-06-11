package com.example.cloud.service;

import com.example.cloud.dto.*;
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


    S3DetailsResponse getS3Details(
            UUID cloudAccountId,
            String bucketName,
            String email
    );

    RdsDetailsResponse getRdsDetails(
            UUID cloudAccountId,
            String dbIdentifier,
            String email
    );

    EksDetailsResponse getEksDetails(
            UUID cloudAccountId,
            String clusterName,
            String email
    );


}