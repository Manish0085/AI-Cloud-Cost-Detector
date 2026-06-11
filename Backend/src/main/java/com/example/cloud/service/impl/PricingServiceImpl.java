package com.example.cloud.service.impl;

import com.example.cloud.service.PricingService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PricingServiceImpl
        implements PricingService {

    private static final Map<String, Double>
            EC2_PRICES = Map.of(

            "t3.micro", 0.0104,
            "t3.small", 0.0208,
            "t3.medium", 0.0416,
            "t3.large", 0.0832
    );

    @Override
    public double getEc2HourlyPrice(
            String instanceType,
            String region
    ) {

        return EC2_PRICES.getOrDefault(
                instanceType,
                0.0
        );
    }
}
