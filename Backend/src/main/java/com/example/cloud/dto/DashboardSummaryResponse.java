package com.example.cloud.dto;

public record DashboardSummaryResponse(

        int totalResources,

        int ec2Count,

        int s3Count,

        int rdsCount,

        int eksCount,

        int totalFindings
) {
}