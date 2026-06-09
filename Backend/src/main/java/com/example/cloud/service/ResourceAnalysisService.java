package com.example.cloud.service;

import com.example.cloud.dto.ResourceFinding;

import java.util.List;
import java.util.UUID;

public interface ResourceAnalysisService {

    List<ResourceFinding> analyzeResource(

            UUID cloudAccountId,

            String resourceId,

            String email
    );
}