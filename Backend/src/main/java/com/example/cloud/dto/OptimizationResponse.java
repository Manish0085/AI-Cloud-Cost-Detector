package com.example.cloud.dto;


public record OptimizationResponse(

        String resourceId,

        String currentInstanceType,

        String aiRecommendation
) {
}