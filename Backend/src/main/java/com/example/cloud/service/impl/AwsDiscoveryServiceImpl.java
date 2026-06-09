package com.example.cloud.service.impl;

import com.example.cloud.dto.Ec2DetailsResponse;
import com.example.cloud.dto.ResourceMetricsResponse;
import com.example.cloud.dto.ResourceResponse;
import com.example.cloud.entity.CloudAccount;
import com.example.cloud.enums.ResourceType;
import com.example.cloud.exception.CloudAccountAccessDeniedException;
import com.example.cloud.exception.CloudAccountNotFoundException;
import com.example.cloud.exception.ResourceNotFoundException;
import com.example.cloud.respository.CloudAccountRepository;
import com.example.cloud.service.AwsDiscoveryService;
import com.example.cloud.service.AwsRegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwsDiscoveryServiceImpl
        implements AwsDiscoveryService {

    private final CloudAccountRepository cloudAccountRepository;
    private final AwsRegionService awsRegionService;

    @Override
    public List<ResourceResponse> discoverResources(
            UUID cloudAccountId,
            ResourceType type,
            String email
    ) {

        CloudAccount account =
                cloudAccountRepository
                        .findByIdAndUserEmail(
                                cloudAccountId,
                                email
                        )
                        .orElseThrow(
                                () -> new CloudAccountAccessDeniedException(
                                        "Access Denied"
                                )
                        );

        if (!account.getUser()
                .getEmail()
                .equals(email)) {

            throw new RuntimeException(
                    "You are not authorized to access this cloud account"
            );
        }

        AwsBasicCredentials credentials =
                AwsBasicCredentials.create(
                        account.getAccessKey(),
                        account.getSecretKey()
                );

        List<Region> regions =
                awsRegionService.getAllRegions(
                        credentials
                );

        return switch (type) {

            case EC2 ->
                    discoverEc2(
                            credentials,
                            regions
                    );

            case RDS ->
                    List.of();

            case S3 ->
                    List.of();

            case EKS ->
                    List.of();

            case ALL -> {

                List<ResourceResponse> resources =
                        new ArrayList<>();

                resources.addAll(
                        discoverEc2(
                                credentials,
                                regions
                        )
                );

                yield resources;
            }
        };
    }

    private List<ResourceResponse> discoverEc2(
            AwsBasicCredentials credentials,
            List<Region> regions
    ) {

        List<ResourceResponse> resources =
                new ArrayList<>();

        for (Region region : regions) {

            try (Ec2Client ec2Client =
                         Ec2Client.builder()
                                 .credentialsProvider(
                                         StaticCredentialsProvider.create(
                                                 credentials
                                         )
                                 )
                                 .region(region)
                                 .build()) {

                DescribeInstancesResponse response =
                        ec2Client.describeInstances();

                for (Reservation reservation :
                        response.reservations()) {

                    for (Instance instance :
                            reservation.instances()) {

                        resources.add(
                                new ResourceResponse(
                                        instance.instanceId(),
                                        getInstanceName(instance),
                                        ResourceType.EC2.name(),
                                        region.id(),
                                        instance.state()
                                                .nameAsString()
                                )
                        );
                    }
                }

            } catch (Exception ex) {

                log.warn(
                        "Failed to scan EC2 resources in region {}",
                        region.id(),
                        ex
                );
            }
        }

        return resources;
    }

    private String getInstanceName(
            Instance instance
    ) {

        return instance.tags()
                .stream()
                .filter(tag ->
                        "Name".equals(tag.key()))
                .map(tag -> tag.value())
                .findFirst()
                .orElse(instance.instanceId());
    }

    @Override
    public Ec2DetailsResponse getEc2Details(
            UUID cloudAccountId,
            String resourceId,
            String email
    ) {

        CloudAccount account =
                cloudAccountRepository
                        .findById(cloudAccountId)
                        .orElseThrow(() ->
                                new CloudAccountNotFoundException(
                                        "Cloud account not found"
                                ));

        if (!account.getUser()
                .getEmail()
                .equals(email)) {

            throw new CloudAccountAccessDeniedException(
                    "Access denied"
            );
        }

        AwsBasicCredentials credentials =
                AwsBasicCredentials.create(
                        account.getAccessKey(),
                        account.getSecretKey()
                );

        List<Region> regions =
                awsRegionService.getAllRegions(
                        credentials
                );

        for (Region region : regions) {

            try (Ec2Client ec2Client =
                         Ec2Client.builder()
                                 .credentialsProvider(
                                         StaticCredentialsProvider.create(
                                                 credentials
                                         )
                                 )
                                 .region(region)
                                 .build()) {

                DescribeInstancesResponse response =
                        ec2Client.describeInstances();

                for (Reservation reservation :
                        response.reservations()) {

                    for (Instance instance :
                            reservation.instances()) {

                        if (instance.instanceId()
                                .equals(resourceId)) {

                            return new Ec2DetailsResponse(
                                    instance.instanceId(),
                                    getInstanceName(instance),
                                    instance.instanceTypeAsString(),
                                    instance.state().nameAsString(),
                                    instance.publicIpAddress(),
                                    instance.privateIpAddress(),

                                    instance.vpcId(),
                                    instance.subnetId(),
                                    instance.placement().availabilityZone()
                            );
                        }
                    }
                }

            } catch (Exception ex) {

                log.warn(
                        "Failed scanning region {}",
                        region.id(),
                        ex
                );
            }
        }

        throw new ResourceNotFoundException(
                "EC2 instance not found"
        );
    }


    @Override
    public ResourceMetricsResponse getMetrics(
            UUID cloudAccountId,
            String resourceId,
            String email
    ) {

        CloudAccount account =
                cloudAccountRepository.findById(cloudAccountId)
                        .orElseThrow(
                                () -> new CloudAccountNotFoundException(
                                        "Cloud account not found"
                                )
                        );

        if (!account.getUser()
                .getEmail()
                .equals(email)) {

            throw new RuntimeException(
                    "Access denied"
            );
        }

        AwsBasicCredentials credentials =
                AwsBasicCredentials.create(
                        account.getAccessKey(),
                        account.getSecretKey()
                );

        List<Region> regions =
                awsRegionService.getAllRegions(
                        credentials
                );

        for (Region region : regions) {

            try (Ec2Client ec2Client =
                         Ec2Client.builder()
                                 .credentialsProvider(
                                         StaticCredentialsProvider.create(
                                                 credentials
                                         )
                                 )
                                 .region(region)
                                 .build()) {

                DescribeInstancesResponse response =
                        ec2Client.describeInstances();

                boolean found =
                        response.reservations()
                                .stream()
                                .flatMap(r ->
                                        r.instances().stream())
                                .anyMatch(i ->
                                        i.instanceId()
                                                .equals(resourceId));

                if (!found) {
                    continue;
                }

                try (CloudWatchClient cloudWatchClient =
                             CloudWatchClient.builder()
                                     .credentialsProvider(
                                             StaticCredentialsProvider.create(
                                                     credentials
                                             )
                                     )
                                     .region(region)
                                     .build()) {

                    return new ResourceMetricsResponse(

                            getAverageMetric(
                                    cloudWatchClient,
                                    "CPUUtilization",
                                    resourceId
                            ),

                            getAverageMetric(
                                    cloudWatchClient,
                                    "NetworkIn",
                                    resourceId
                            ),

                            getAverageMetric(
                                    cloudWatchClient,
                                    "NetworkOut",
                                    resourceId
                            ),

                            getAverageMetric(
                                    cloudWatchClient,
                                    "DiskReadBytes",
                                    resourceId
                            ),

                            getAverageMetric(
                                    cloudWatchClient,
                                    "DiskWriteBytes",
                                    resourceId
                            )
                    );
                }

            } catch (Exception ex) {

                log.warn(
                        "Failed to fetch metrics from region {}",
                        region.id(),
                        ex
                );
            }
        }

        throw new ResourceNotFoundException(
                "Resource not found"
        );
    }

    private Double getAverageMetric(
            CloudWatchClient cloudWatchClient,
            String metricName,
            String instanceId
    ) {

        GetMetricStatisticsRequest request =
                GetMetricStatisticsRequest.builder()
                        .namespace("AWS/EC2")
                        .metricName(metricName)
                        .startTime(
                                Instant.now()
                                        .minus(7, ChronoUnit.DAYS)
                        )
                        .endTime(
                                Instant.now()
                        )
                        .period(3600)
                        .statistics(
                                Statistic.AVERAGE
                        )
                        .dimensions(
                                Dimension.builder()
                                        .name("InstanceId")
                                        .value(instanceId)
                                        .build()
                        )
                        .build();

        GetMetricStatisticsResponse response =
                cloudWatchClient.getMetricStatistics(
                        request
                );

        return response.datapoints()
                .stream()
                .map(Datapoint::average)
                .max(Double::compareTo)
                .orElse(0.0);
    }
}