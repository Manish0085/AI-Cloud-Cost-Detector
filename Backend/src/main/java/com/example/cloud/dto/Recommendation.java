package com.example.cloud.dto;

public record Recommendation(

        String category,

        String recommendation,

        String reason

) {
}