package com.example.cloud.service;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;

import java.util.List;

public interface AwsRegionService {

    List<Region> getAllRegions(
            AwsBasicCredentials credentials
    );
}