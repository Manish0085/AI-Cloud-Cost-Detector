package com.example.cloud.dto;

public record EksDetailsResponse(

        String clusterName,

        String version,

        String status,

        String endpoint,

        Integer nodeCount

) {
}