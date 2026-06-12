package com.example.cloud.dto;

import java.io.Serializable;

public record RdsDetailsResponse(

        String dbIdentifier,

        String engine,

        String engineVersion,

        String instanceClass,

        String status,

        Integer allocatedStorage,

        String availabilityZone

) implements Serializable {
}