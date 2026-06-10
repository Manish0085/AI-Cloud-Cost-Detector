package com.example.cloud.service.impl;

import com.example.cloud.dto.ResourceFinding;
import com.example.cloud.dto.ResourceOptimizationResult;
import com.example.cloud.service.AiRecommendationService;
import com.example.cloud.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiRecommendationServiceImpl
        implements AiRecommendationService {

    private final ChatClient chatClient;
    private final RagService ragService;

    @Override
    public String generateResourceRecommendation(

            String resourceName,

            List<ResourceFinding> findings
    ) {

        String findingsText =
                buildFindingsText(findings);

        String query =
                findings.stream()
                        .map(ResourceFinding::reason)
                        .collect(
                                java.util.stream.Collectors.joining(" ")
                        );

        String ragContext =
                ragService.retrieveContext(
                        query
                );

        String prompt = """

            You are a Senior AWS FinOps Architect.

            Use the AWS Best Practices provided below
            when generating recommendations.

            AWS Best Practices:

            %s

            Resource Name:
            %s

            Resource Findings:

            %s

            Generate:

            1. Current Situation
            2. Recommendation
            3. Business Impact
            4. Potential Savings

            Keep the response under 120 words.

            """.formatted(

                ragContext,

                resourceName,

                findingsText
        );

        return chatClient.prompt()
                .user(prompt)
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

    private String buildFindingsText(
            List<ResourceFinding> findings
    ) {

        StringBuilder findingsText =
                new StringBuilder();

        for (ResourceFinding finding : findings) {

            findingsText.append("""

                Resource ID: %s
                Resource Type: %s
                Recommendation: %s
                Reason: %s

                """.formatted(

                    finding.resourceId(),
                    finding.resourceType(),
                    finding.recommendation(),
                    finding.reason()
            ));
        }

        return findingsText.toString();
    }
}