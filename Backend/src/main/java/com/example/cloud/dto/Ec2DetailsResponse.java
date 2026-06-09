package com.example.cloud.dto;

public record Ec2DetailsResponse(

        String instanceId,

        String instanceName,

        String instanceType,

        String state,

        String publicIp,

        String privateIp,

        String vpcId,

        String subnetId,

        String availabilityZone

) {
}