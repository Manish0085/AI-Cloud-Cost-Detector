package com.example.cloud.dto;

import java.io.Serializable;

public record EksDetailsResponse(

        String clusterName,

        String version,

        String status,

        String endpoint,

        Integer nodeCount

) implements Serializable {
}