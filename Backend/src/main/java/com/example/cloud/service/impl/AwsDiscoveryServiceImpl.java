package com.example.cloud.service.impl;

import com.example.cloud.dto.*;
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
import software.amazon.awssdk.services.eks.EksClient;
import software.amazon.awssdk.services.eks.model.*;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.DBInstance;
import software.amazon.awssdk.services.rds.model.DescribeDbInstancesRequest;
import software.amazon.awssdk.services.rds.model.DescribeDbInstancesResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

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
                    discoverRds(credentials, regions);

            case S3 ->
                    discoverS3(
                            credentials
                    );


            case EKS ->
                    discoverEks(credentials, regions);

            case ALL -> {

                List<ResourceResponse> resources =
                        new ArrayList<>();

                resources.addAll(
                        discoverEc2(
                                credentials,
                                regions
                        )
                );

                resources.addAll(
                        discoverS3(
                                credentials
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


    private List<ResourceResponse> discoverS3(
            AwsBasicCredentials credentials
    ) {

        S3Client s3Client =
                S3Client.builder()
                        .credentialsProvider(
                                StaticCredentialsProvider.create(
                                        credentials
                                )
                        )
                        .region(Region.US_EAST_1)
                        .build();

        ListBucketsResponse response =
                s3Client.listBuckets();

        return response.buckets()
                .stream()
                .map(bucket ->
                        new ResourceResponse(
                                bucket.name(),      // resourceId
                                bucket.name(),      // resourceName
                                ResourceType.S3.name(),
                                "GLOBAL",
                                "ACTIVE"
                        )
                )
                .toList();
    }

    private List<ResourceResponse> discoverRds(
            AwsBasicCredentials credentials,
            List<Region> regions
    ) {

        List<ResourceResponse> resources =
                new ArrayList<>();

        for (Region region : regions) {

            try {

                RdsClient rdsClient =
                        RdsClient.builder()
                                .credentialsProvider(
                                        StaticCredentialsProvider.create(
                                                credentials
                                        )
                                )
                                .region(region)
                                .build();

                DescribeDbInstancesResponse response =
                        rdsClient.describeDBInstances();

                for (DBInstance db : response.dbInstances()) {

                    resources.add(
                            new ResourceResponse(
                                    db.dbInstanceIdentifier(),
                                    db.dbInstanceIdentifier(),
                                    ResourceType.RDS.name(),
                                    region.id(),
                                    db.dbInstanceStatus()
                            )
                    );
                }

            } catch (Exception ex) {

                log.warn(
                        "Failed to discover RDS instances in region {}",
                        region.id()
                );
            }
        }

        return resources;
    }


    private List<ResourceResponse> discoverEks(
            AwsBasicCredentials credentials,
            List<Region> regions
    ) {

        List<ResourceResponse> resources =
                new ArrayList<>();

        for (Region region : regions) {

            try {

                EksClient eksClient =
                        EksClient.builder()
                                .credentialsProvider(
                                        StaticCredentialsProvider.create(
                                                credentials
                                        )
                                )
                                .region(region)
                                .build();

                ListClustersResponse response =
                        eksClient.listClusters();

                for (String clusterName : response.clusters()) {

                    DescribeClusterResponse cluster =
                            eksClient.describeCluster(
                                    request ->
                                            request.name(
                                                    clusterName
                                            )
                            );

                    resources.add(
                            new ResourceResponse(
                                    clusterName,
                                    clusterName,
                                    ResourceType.EKS.name(),
                                    region.id(),
                                    cluster.cluster()
                                            .status()
                                            .toString()
                            )
                    );
                }

            } catch (Exception ex) {

                log.warn(
                        "Failed to discover EKS clusters in region {}",
                        region.id()
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



    @Override
    public S3DetailsResponse getS3Details(

            UUID cloudAccountId,

            String bucketName,

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

        try (S3Client s3Client =
                     S3Client.builder()
                             .credentialsProvider(
                                     StaticCredentialsProvider.create(
                                             credentials
                                     )
                             )
                             .region(Region.US_EAST_1)
                             .build()) {

            String region =
                    s3Client.getBucketLocation(
                                    GetBucketLocationRequest.builder()
                                            .bucket(bucketName)
                                            .build()
                            )
                            .locationConstraintAsString();

            if (region == null || region.isBlank()) {

                region = "us-east-1";
            }

            boolean versioningEnabled = false;

            try {

                GetBucketVersioningResponse versioning =
                        s3Client.getBucketVersioning(
                                GetBucketVersioningRequest.builder()
                                        .bucket(bucketName)
                                        .build()
                        );

                versioningEnabled =
                        versioning.status() ==
                                BucketVersioningStatus.ENABLED;

            } catch (Exception ignored) {
            }

            boolean publicAccessBlocked = true;

            try {

                GetPublicAccessBlockResponse response =
                        s3Client.getPublicAccessBlock(
                                GetPublicAccessBlockRequest.builder()
                                        .bucket(bucketName)
                                        .build()
                        );

                PublicAccessBlockConfiguration config =
                        response.publicAccessBlockConfiguration();

                publicAccessBlocked =
                        config.blockPublicAcls()
                                && config.blockPublicPolicy()
                                && config.ignorePublicAcls()
                                && config.restrictPublicBuckets();

            } catch (Exception ex) {

                publicAccessBlocked = false;
            }

            return new S3DetailsResponse(

                    bucketName,

                    region,

                    versioningEnabled,

                    publicAccessBlocked,

                    0L,

                    0L
            );
        }
    }


    @Override
    public EksDetailsResponse getEksDetails(

            UUID cloudAccountId,

            String clusterName,

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

            try {

                EksClient eksClient =
                        EksClient.builder()
                                .credentialsProvider(
                                        StaticCredentialsProvider.create(
                                                credentials
                                        )
                                )
                                .region(region)
                                .build();

                List<String> clusters =
                        eksClient.listClusters()
                                .clusters();

                if (!clusters.contains(clusterName)) {
                    continue;
                }

                Cluster cluster =
                        eksClient.describeCluster(
                                        DescribeClusterRequest.builder()
                                                .name(clusterName)
                                                .build()
                                )
                                .cluster();

                int nodeCount = 0;

                try {

                    nodeCount =
                            eksClient.listNodegroups(
                                            ListNodegroupsRequest.builder()
                                                    .clusterName(clusterName)
                                                    .build()
                                    )
                                    .nodegroups()
                                    .size();

                } catch (Exception ignored) {
                }

                return new EksDetailsResponse(

                        cluster.name(),

                        cluster.version(),

                        cluster.statusAsString(),

                        cluster.endpoint(),

                        nodeCount
                );

            } catch (Exception ex) {

                log.warn(
                        "Failed to search EKS cluster {} in region {}",
                        clusterName,
                        region.id()
                );
            }
        }

        throw new ResourceNotFoundException(
                "EKS cluster not found: " + clusterName
        );
    }


    @Override
    public RdsDetailsResponse getRdsDetails(

            UUID cloudAccountId,

            String dbIdentifier,

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

            try {

                RdsClient rdsClient =
                        RdsClient.builder()
                                .credentialsProvider(
                                        StaticCredentialsProvider.create(
                                                credentials
                                        )
                                )
                                .region(region)
                                .build();

                DescribeDbInstancesResponse response =
                        rdsClient.describeDBInstances();

                for (DBInstance dbInstance :
                        response.dbInstances()) {

                    if (dbInstance
                            .dbInstanceIdentifier()
                            .equals(dbIdentifier)) {

                        return new RdsDetailsResponse(

                                dbInstance
                                        .dbInstanceIdentifier(),

                                dbInstance.engine(),

                                dbInstance.engineVersion(),

                                dbInstance.dbInstanceClass(),

                                dbInstance.dbInstanceStatus(),

                                dbInstance.allocatedStorage(),

                                dbInstance.availabilityZone()
                        );
                    }
                }

            } catch (Exception ex) {

                log.warn(
                        "Failed to search RDS instance {} in region {}",
                        dbIdentifier,
                        region.id()
                );
            }
        }

        throw new ResourceNotFoundException(
                "RDS instance not found: " + dbIdentifier
        );
    }
}