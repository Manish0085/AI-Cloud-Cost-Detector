package com.example.cloud.dto;

import java.io.Serializable;

public record S3DetailsResponse(

        String bucketName,

        String region,

        boolean versioningEnabled,

        boolean publicAccessBlocked,

        Long objectCount,

        Long bucketSizeBytes

) implements Serializable {
}