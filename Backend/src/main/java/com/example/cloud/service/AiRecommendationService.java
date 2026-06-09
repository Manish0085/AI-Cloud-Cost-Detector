package com.example.cloud.service;

import com.example.cloud.dto.Ec2DetailsResponse;
import com.example.cloud.dto.Recommendation;
import com.example.cloud.dto.ResourceMetricsResponse;

import java.util.List;

public interface AiRecommendationService {

    String generateRecommendation(

            Ec2DetailsResponse details,

            ResourceMetricsResponse metrics,

            List<Recommendation> recommendations
    );
}