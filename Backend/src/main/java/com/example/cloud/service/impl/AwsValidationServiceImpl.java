package com.example.cloud.service.impl;

import com.example.cloud.dto.AwsIdentity;
import com.example.cloud.exception.InvalidCloudCredentialException;
import com.example.cloud.service.AwsValidationService;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityRequest;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;

@Service
public class AwsValidationServiceImpl
        implements AwsValidationService {

    @Override
    public AwsIdentity validateCredentials(
            String accessKey,
            String secretKey
    ) {

        try {

            AwsBasicCredentials credentials =
                    AwsBasicCredentials.create(
                            accessKey,
                            secretKey
                    );

            StsClient stsClient =
                    StsClient.builder()
                            .credentialsProvider(
                                    StaticCredentialsProvider.create(
                                            credentials
                                    )
                            )
                            .region(Region.US_EAST_1)
                            .build();

            GetCallerIdentityResponse response =
                    stsClient.getCallerIdentity(
                            GetCallerIdentityRequest.builder()
                                    .build()
                    );

            return new AwsIdentity(
                    response.account(),
                    response.arn()
            );

        } catch (Exception ex) {

            throw new InvalidCloudCredentialException(
                    "Invalid AWS credentials"
            );
        }
    }
}