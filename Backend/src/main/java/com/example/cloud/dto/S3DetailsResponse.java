package com.example.cloud.dto;

public record S3DetailsResponse(

        String bucketName,

        String region,

        boolean versioningEnabled,

        boolean publicAccessBlocked,

        Long objectCount,

        Long bucketSizeBytes

) {
}