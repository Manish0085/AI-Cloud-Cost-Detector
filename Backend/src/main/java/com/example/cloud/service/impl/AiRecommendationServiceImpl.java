package com.example.cloud.service.impl;

import com.example.cloud.dto.ResourceFinding;
import com.example.cloud.dto.ResourceOptimizationResult;
import com.example.cloud.service.AiRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiRecommendationServiceImpl
        implements AiRecommendationService {

    private final ChatClient chatClient;

    @Override
    public String generateResourceRecommendation(

            String resourceName,

            List<ResourceFinding> findings
    ) {

        StringBuilder prompt =
                new StringBuilder();

        prompt.append("""
                You are a Senior AWS FinOps Architect.

                Analyze the findings for this AWS resource.

                Provide:
                1. Current Situation
                2. Recommendation
                3. Potential Savings

                Keep the response under 100 words.

                Resource Name:
                """);

        prompt.append(resourceName)
                .append("\n\nFindings:\n");

        findings.forEach(finding ->

                prompt.append("""
                        
                        Recommendation: %s
                        Reason: %s
                        
                        """.formatted(
                        finding.recommendation(),
                        finding.reason()
                ))
        );

        return chatClient.prompt()
                .user(prompt.toString())
                .call()
                .content();
    }

    @Override
    public String generateExecutiveSummary(

            List<ResourceOptimizationResult> resources
    ) {

        StringBuilder prompt =
                new StringBuilder();

        prompt.append("""
                You are a Senior Cloud Cost Optimization Architect.

                Analyze all resource optimization results.

                Generate:

                1. Executive Summary
                2. Top Cost Risks
                3. Priority Actions
                4. Estimated Savings

                Keep the response under 200 words.

                Resources:
                
                """);

        resources.forEach(resource ->

                prompt.append("""
                        
                        Resource: %s
                        
                        AI Recommendation:
                        %s
                        
                        """.formatted(
                        resource.resourceName(),
                        resource.aiRecommendation()
                ))
        );

        return chatClient.prompt()
                .user(prompt.toString())
                .call()
                .content();
    }
}