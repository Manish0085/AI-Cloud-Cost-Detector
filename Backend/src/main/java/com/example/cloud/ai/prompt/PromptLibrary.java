package com.example.cloud.ai.prompt;

import org.springframework.stereotype.Component;

/**
 * Central, versionable home for every LLM prompt used by the application.
 *
 * <p>Prompts are intentionally separated from the AI invocation logic
 * ({@code AiRecommendationServiceImpl}) so they can be reviewed, diffed and
 * evolved independently of the code that calls the model. Each prompt is a
 * pure function of its inputs and performs no I/O.
 *
 * <ul>
 *   <li>{@link #recommendationPrompt} — per-resource FinOps recommendation (RAG-grounded).</li>
 *   <li>{@link #executiveSummaryPrompt} — account-level executive summary.</li>
 * </ul>
 */
@Component
public class PromptLibrary {

    /** Bump when the recommendation prompt wording changes (for traceability). */
    public static final String RECOMMENDATION_PROMPT_VERSION = "v1";
    public static final String EXECUTIVE_SUMMARY_PROMPT_VERSION = "v1";

    private static final String RECOMMENDATION_SYSTEM = """
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

    """;

    private static final String RECOMMENDATION_FORMAT = """

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

    private static final String EXECUTIVE_SUMMARY_HEADER = """
                You are a Senior Cloud Cost Optimization Architect.

                Analyze all resource optimization results.

                Generate:

                1. Executive Summary
                2. Top Cost Risks
                3. Priority Actions
                4. Estimated Savings

                Keep the response under 200 words.

                Resources:

                """;

    /**
     * Builds the RAG-grounded, per-resource recommendation prompt.
     *
     * @param ragContext   knowledge-base context retrieved for the findings
     * @param resourceName the resource under analysis
     * @param findingsText pre-formatted deterministic findings
     */
    public String recommendationPrompt(
            String ragContext,
            String resourceName,
            String findingsText
    ) {
        return RECOMMENDATION_SYSTEM
                + ragContext
                + "\n\n    Resource Name:\n\n    "
                + resourceName
                + "\n\n    Resource Findings:\n\n    "
                + findingsText
                + RECOMMENDATION_FORMAT;
    }

    /**
     * Builds the account-level executive-summary prompt.
     *
     * @param resourcesBlock pre-formatted per-resource recommendation block
     */
    public String executiveSummaryPrompt(String resourcesBlock) {
        return EXECUTIVE_SUMMARY_HEADER + resourcesBlock;
    }
}
