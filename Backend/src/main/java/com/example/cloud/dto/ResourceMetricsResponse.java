package com.example.cloud.dto;

public record ResourceMetricsResponse(

        Double cpuUtilization,

        Double networkIn,

        Double networkOut,

        Double diskReadBytes,

        Double diskWriteBytes
) {
}