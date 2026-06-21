#!/bin/bash

set -e

# ==============================
# Configuration
# ==============================

INSTANCE_NAME="Cloud Optimizer Server"
AMI_ID="ami-01a00762f46d584a1"
INSTANCE_TYPE="m7i-flex.large"
KEY_PAIR_NAME="cloud_optimizer_server_v2"
SECURITY_GROUP_ID="sg-08e41f807ca78088c"
AWS_REGION="ap-south-1"

# ==============================
# Logging Functions
# ==============================

log_info() {
    echo "[INFO] $1"
}

log_error() {
    echo "[ERROR] $1"
}

# ==============================
# Validation Functions
# ==============================

check_aws_cli() {
    log_info "Checking AWS CLI..."

    if ! command -v aws &> /dev/null
    then
        log_error "AWS CLI not found."
        exit 1
    fi

    log_info "AWS CLI found."
}

check_aws_credentials() {
    log_info "Checking AWS Credentials..."

    if ! aws sts get-caller-identity > /dev/null 2>&1
    then
        log_error "AWS credentials not configured."
        exit 1

    fi

    log_info "AWS credentials verified."
}

verify_key_pair() {
    log_info "Verifying key pair..."

    if ! aws ec2 describe-key-pairs \
        --key-names "$KEY_PAIR_NAME" \
        --region "$AWS_REGION" > /dev/null
    then
        log_error "Key Pair not found."
        exit 1
    fi

    log_info "Key pair exists."
}

verify_security_group() {
    log_info "Verifying security group..."

    if ! aws ec2 describe-security-groups \
        --group-ids "$SECURITY_GROUP_ID" \
        --region "$AWS_REGION" > /dev/null
    then
        log_error "Security Group does not exist"
        exit 1
    fi

    log_info "Security group exists."
}

# ==============================
# Create the Key-Pair EC2
# ==============================

create_key_pair_if_not_exists() {
    log_info "Creating the Key-Pair for the ec2 instance $INSTANCE_NAME"

    if aws ec2 describe-key-pairs \
             --key-names "$KEY_PAIR_NAME" \
             --region "$AWS_REGION" &> /dev/null
    then
        log_info "Key Pair already exists."
        return;
    fi

    log_info "Creating Key Pair..."

    mkdir -p keys

    if ! aws ec2 create-key-pair \
              --key-name "$KEY_PAIR_NAME" \
              --query 'KeyMaterial' \
              --region "$AWS_REGION" \
              --output text > "keys/$KEY_PAIR_NAME.pem"
    then
        log_error "Failed to create Key Pair."
        exit 1
    fi

    chmod 400 "keys/$KEY_PAIR_NAME.pem"

    log_info "Key Pair created successfully."
}

# ==============================
# Launch EC2
# ==============================

launch_instance() {

    log_info "Launching EC2 instance..."

    if ! INSTANCE_ID=$(aws ec2 run-instances \
        --image-id "$AMI_ID" \
        --instance-type "$INSTANCE_TYPE" \
        --key-name "$KEY_PAIR_NAME" \
        --security-group-ids "$SECURITY_GROUP_ID" \
        --tag-specifications "ResourceType=instance,Tags=[{Key=Name,Value=$INSTANCE_NAME}]" \
        --query 'Instances[0].InstanceId' \
        --output text \
        --region "$AWS_REGION")
    then
        log_error "Failed to launch the instance"
        exit 1
    fi

    log_info "Instance Created: $INSTANCE_ID"
}

# ==============================
# Wait For Running
# ==============================

wait_for_instance() {

    log_info "Waiting for instance to enter RUNNING state..."

    if ! aws ec2 wait instance-running \
        --instance-ids "$INSTANCE_ID" \
        --region "$AWS_REGION" &> /dev/null
    then
        log_error "Instance failed to reach RUNNING state."
        exit 1
    fi
    log_info "Instance is RUNNING."
}

# ==============================
# Get Public IP
# ==============================

fetch_public_ip() {

    if ! PUBLIC_IP=$(aws ec2 describe-instances \
        --instance-ids "$INSTANCE_ID" \
        --query 'Reservations[0].Instances[0].PublicIpAddress' \
        --output text \
        --region "$AWS_REGION")
    then
        log_error "Failed to fetch the Public IP"
        exit 1
    fi

    log_info "Public IP: $PUBLIC_IP"
}

# ==============================
# Main
# ==============================

main() {

    check_aws_cli
    check_aws_credentials
    create_key_pair_if_not_exists
    verify_key_pair
    verify_security_group

    launch_instance
    wait_for_instance
    fetch_public_ip

    echo ""
    echo "====================================="
    echo "Instance ID : $INSTANCE_ID"
    echo "Public IP   : $PUBLIC_IP"
    echo "====================================="
}

main