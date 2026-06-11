package com.example.cloud.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "AI Cloud Cost Optimizer API",
                version = "1.0",
                description = """
                        AI-powered AWS Cloud Cost Optimization Platform.
                        
                        Features:
                        - AWS Resource Discovery
                        - CloudWatch Metrics Analysis
                        - Rule-Based Optimization
                        - RAG (Spring AI + PGVector)
                        - OpenRouter AI Recommendations
                        - Optimization Reports
                        """,
                contact = @Contact(
                        name = "Manish Kumar"
                )
        )
)
public class OpenApiConfig {
}