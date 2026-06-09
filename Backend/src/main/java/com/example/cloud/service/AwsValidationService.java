package com.example.cloud.service;

import com.example.cloud.dto.AwsIdentity;

public interface AwsValidationService {

    AwsIdentity validateCredentials(
            String accessKey,
            String secretKey
    );
}