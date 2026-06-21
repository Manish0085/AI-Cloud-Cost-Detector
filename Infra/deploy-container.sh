#!/bin/bash

set -e

# ==========================================
# Configuration
# ==========================================

PUBLIC_IP="$1"
KEY_FILE="$2"

REMOTE_USER="ubuntu"
REMOTE_DIR="/home/ubuntu/cloud-optimizer"

# ==========================================
# Logging Functions
# ==========================================

log_info() {
    echo "[INFO] $1"
}

log_error() {
    echo "[ERROR] $1"
}

# ==========================================
# Validation
# ==========================================

validate_inputs() {

    if [ -z "$PUBLIC_IP" ]; then
        log_error "Public IP is required."
        exit 1
    fi

    if [ -z "$KEY_FILE" ]; then
        log_error "PEM file path is required."
        exit 1
    fi

    if [ ! -f "$KEY_FILE" ]; then
        log_error "PEM file not found."
        exit 1
    fi

    if [ ! -f "docker-compose.yml" ]; then
        log_error "docker-compose.yml not found."
        exit 1
    fi

    log_info "Inputs validated."
}

# ==========================================
# SSH Utility
# ==========================================

run_remote_command() {

    ssh \
        -o StrictHostKeyChecking=no \
        -i "$KEY_FILE" \
        "$REMOTE_USER@$PUBLIC_IP" \
        "$1"
}

# ==========================================
# Create Deployment Directory
# ==========================================

create_remote_directory() {

    log_info "Creating deployment directory..."

    if ! run_remote_command "
        mkdir -p $REMOTE_DIR
    "
    then
        log_error "Failed to create remote directory."
        exit 1
    fi

    log_info "Deployment directory created."
}

# ==========================================
# Copy Docker Compose
# ==========================================

copy_docker_compose() {

    log_info "Copying docker-compose.yml..."

    if [ ! -f docker-compose.yml ]; then
        log_error "docker-compose.yml does not exits"
        exit 1
    fi

    if ! scp \
        -o StrictHostKeyChecking=no \
        -i "$KEY_FILE" \
        ./docker-compose.yml \
        "$REMOTE_USER@$PUBLIC_IP:$REMOTE_DIR/docker-compose.yml"
    then
        log_error "Failed to copy docker-compose.yml"
        exit 1
    fi

    log_info "docker-compose.yml copied successfully."
}


copy_env_file() {

    log_info "Copying .env..."

    if [ ! -f .env ]; then
        log_error ".env does not exist"
        exit 1
    fi

    if ! scp \
        -o StrictHostKeyChecking=no \
        -i "$KEY_FILE" \
        ./.env \
        "$REMOTE_USER@$PUBLIC_IP:$REMOTE_DIR/.env"
    then
        log_error "Failed to copy .env"
        exit 1
    fi

    log_info ".env copied successfully."
}

# ==========================================
# Pull Images
# ==========================================

pull_images() {

    log_info "Pulling Docker images..."

    if ! run_remote_command "
        cd $REMOTE_DIR &&
        docker compose pull
    "
    then
        log_error "Failed to pull Docker images."
        exit 1
    fi

    log_info "Docker images pulled successfully."
}

# ==========================================
# Start Containers
# ==========================================

start_containers() {

    log_info "Starting containers..."

    if ! run_remote_command "
        cd $REMOTE_DIR &&
        docker compose up -d
    "
    then
        log_error "Failed to start containers."
        exit 1
    fi

    log_info "Containers started successfully."
}

# ==========================================
# Verify Containers
# ==========================================

verify_containers() {

    log_info "Verifying containers..."

    if ! run_remote_command "
        docker ps
    "
    then
        log_error "Container verification failed."
        exit 1
    fi

    log_info "Containers verified successfully."
}

# ==========================================
# Main
# ==========================================

main() {

    validate_inputs

    create_remote_directory

    copy_docker_compose

    copy_env_file

    pull_images

    start_containers

    verify_containers

    log_info "Deployment completed successfully."
}

main