package com.example.cloud.service;

public interface PricingService {

    double getEc2HourlyPrice(
            String instanceType,
            String region
    );

}