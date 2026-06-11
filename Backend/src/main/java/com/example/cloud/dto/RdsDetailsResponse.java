package com.example.cloud.dto;

public record RdsDetailsResponse(

        String dbIdentifier,

        String engine,

        String engineVersion,

        String instanceClass,

        String status,

        Integer allocatedStorage,

        String availabilityZone

) {
}