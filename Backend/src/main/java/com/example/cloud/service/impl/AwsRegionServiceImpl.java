package com.example.cloud.service.impl;

import com.example.cloud.service.AwsRegionService;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;

import java.util.List;

@Service
public class AwsRegionServiceImpl
        implements AwsRegionService {

    @Override
    public List<Region> getAllRegions(
            AwsBasicCredentials credentials
    ) {

        Ec2Client ec2Client =
                Ec2Client.builder()
                        .credentialsProvider(
                                StaticCredentialsProvider.create(
                                        credentials
                                )
                        )
                        .region(Region.US_EAST_1)
                        .build();

        return ec2Client
                .describeRegions()
                .regions()
                .stream()
                .map(region ->
                        Region.of(region.regionName()))
                .toList();
    }
}