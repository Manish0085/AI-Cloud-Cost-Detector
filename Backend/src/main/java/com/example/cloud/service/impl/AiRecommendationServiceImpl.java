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
You are a Principal AWS FinOps Architect and Cloud Cost Optimization Expert.

```
    Your task is to analyze AWS resource metrics and generate accurate, business-oriented recommendations.

    STRICT ANALYSIS RULES:

    1. Never classify a resource as idle based on a single metric.
    2. Evaluate CPU, Memory, Network, and Storage metrics together.
    3. High CPU utilization (>80%) indicates an actively used resource.
    4. Low disk activity alone does NOT mean the resource is idle.
    5. If CPU utilization is high, prioritize performance, scalability, and reliability recommendations over cost-cutting actions.
    6. Recommend termination only when multiple indicators suggest prolonged underutilization.
    7. Avoid contradictory recommendations.
    8. Base conclusions only on the provided metrics.
    9. If metrics indicate an active workload, do not recommend stopping or terminating the resource.
    10. Explain recommendations in business terms, not only technical terms.

    AWS Best Practices:

    """
                + ragContext +

                """
            
                Resource Name:
            
                """
                + resourceName +

                """
            
                Resource Findings:
            
                """
                + findingsText +

                """
            
                Generate the response in exactly this format:
            
                1. Current Situation
                - Summarize the observed utilization pattern.
            
                2. Recommendation
                - Provide the most appropriate optimization action.
            
                3. Business Impact
                - Explain operational, reliability, or cost implications.
            
                4. Potential Savings / Optimization Opportunity
                - Estimate savings only if justified by the metrics.
                - If no direct cost savings are evident, suggest performance optimization opportunities instead.
            
                Additional Guidance:
                - High CPU + low disk activity may indicate compute-intensive workloads.
                - High Network + high CPU may indicate active production traffic.
                - Zero disk activity should not be treated as waste if CPU or network activity is high.
                - When metrics conflict, prioritize CPU and network activity over storage metrics.
            
                Keep the response under 150 words.
                """;

        System.out.println("===== PROMPT =====");
        System.out.println(prompt);
        System.out.println("==================");


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

        System.out.println("===== PROMPT =====");
        System.out.println(prompt);
        System.out.println("==================");

        return chatClient.prompt()
                .user(prompt.toString())
                .call()
                .content();
    }

    private String buildFindingsText(
            List<ResourceFinding> findings
    ) {


        StringBuilder findingsText = new StringBuilder();

        for (ResourceFinding finding : findings) {

            findingsText.append("""
            
            Resource ID: %s
            Resource Type: %s
            Recommendation Type: %s
            Reason: %s
            
            """.formatted(
                    finding.resourceId(),
                    finding.resourceType(),
                    finding.recommendation(),
                    finding.reason() == null
                            ? ""
                            : finding.reason().replace("%", "%%")
            ));
        }

        return findingsText.toString();


    }

}