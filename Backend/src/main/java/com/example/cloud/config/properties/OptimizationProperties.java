package com.example.cloud.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Externalised thresholds for the deterministic FinOps rule engine
 * ({@code ResourceAnalysisServiceImpl}).
 *
 * <p>Every value defaults to the constant that was previously hardcoded, so the
 * analysis behaviour is unchanged unless explicitly overridden under
 * {@code app.optimization.*} in a profile or via an environment variable
 * (e.g. {@code APP_OPTIMIZATION_CPU_HIGH_THRESHOLD}).
 */
@ConfigurationProperties(prefix = "app.optimization")
public record OptimizationProperties(

        /** CPU % above which a workload is treated as actively used. */
        @DefaultValue("80") double cpuHighThreshold,

        /** CPU % below which an instance is a downsizing / idle candidate. */
        @DefaultValue("5") double cpuLowThreshold,

        /** Network bytes (in & out) below which traffic is considered negligible. */
        @DefaultValue("1000") double networkIdleBytes,

        /** Allocated RDS storage (GB) below which storage is flagged as small. */
        @DefaultValue("20") int rdsMinStorageGb,

        /** Fraction of monthly cost assumed recoverable by downsizing. */
        @DefaultValue("0.30") double downsizeSavingsRatio,

        /** Fraction of monthly cost assumed recoverable for low-network workloads. */
        @DefaultValue("0.50") double idleNetworkSavingsRatio,

        /** Billing hours per day used for monthly cost estimation. */
        @DefaultValue("24") int hoursPerDay,

        /** Billing days per month used for monthly cost estimation. */
        @DefaultValue("30") int daysPerMonth
) {
}
