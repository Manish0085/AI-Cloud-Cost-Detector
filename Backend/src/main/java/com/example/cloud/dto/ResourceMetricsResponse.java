package com.example.cloud.dto;

import java.io.Serializable;

public record ResourceMetricsResponse(

        Double cpuUtilization,

        Double networkIn,

        Double networkOut,

        Double diskReadBytes,

        Double diskWriteBytes
) implements Serializable {
}