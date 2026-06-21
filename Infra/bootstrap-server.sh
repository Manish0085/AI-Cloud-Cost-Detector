#!/bin/bash

set -e

# ==========================================
# Configuration
# ==========================================

PUBLIC_IP="$1"
KEY_FILE="$2"

REMOTE_USER="ubuntu"

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
        log_error "PEM file not found: $KEY_FILE"
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
# Wait For SSH
# ==========================================

wait_for_ssh() {

    log_info "Waiting for SSH access..."

    until ssh \
        -o StrictHostKeyChecking=no \
        -i "$KEY_FILE" \
        "$REMOTE_USER@$PUBLIC_IP" \
        "echo SSH_READY" > /dev/null 2>&1
    do
        log_info "SSH not ready. Retrying in 10 seconds..."
        sleep 10
    done

    log_info "SSH connection established."
}

# ==========================================
# Update Server
# ==========================================

update_server() {

    log_info "Updating Ubuntu packages..."

    if ! run_remote_command "
        sudo apt update -y &&
        sudo apt upgrade -y
    "
    then
        log_error "Failed to update server server"
        exit 1
    fi

    log_info "Server updated successfully."
}

# ==========================================
# Install Git
# ==========================================

install_git() {

    log_info "Installing Git..."

    if ! run_remote_command "
        sudo apt install git -y
    "
    then
        log_error "Failed to install git"
        exit 1
    fi

    log_info "Git installed successfully."
}

# ==========================================
# Install Java 21
# ==========================================

#install_java() {
#
#    log_info "Installing Java 21..."
#
#    if ! run_remote_command "
#        sudo apt install openjdk-21-jdk -y
#    "
#    then
#        log_error "Failed to install the java"
#        exit 1
#    fi
#
#    log_info "Java installed successfully."
#}

# ==========================================
# Install Node.js 22
# ==========================================

#install_node() {
#
#    log_info "Installing Node.js..."
#
#    if ! run_remote_command "
#        curl -fsSL https://deb.nodesource.com/setup_22.x | sudo -E bash -
#        sudo apt install nodejs -y
#    "
#    then
#        log_error "Failed to install the node"
#        exit 1
#    fi
#
#    log_info "Node.js installed successfully."
#}

# ==========================================
# Install Docker
# ==========================================

install_docker() {

    log_info "Installing Docker..."

    if ! run_remote_command "
        sudo apt install docker.io -y &&
        sudo systemctl enable docker &&
        sudo systemctl start docker &&
        sudo usermod -aG docker ubuntu
    "
    then
        log_error "Failed to install the docker"
        exit 1
    fi

    log_info "Docker installed successfully."
}

# ==========================================
# Install Docker Compose
# ==========================================

install_docker_compose() {

    log_info "Installing Docker Compose..."

    if ! run_remote_command "
        sudo apt update -y &&
        sudo apt install docker-compose-v2 -y
    "
    then
        log_error "Failed to install the docker compose"
        exit 1
    fi

    log_info "Docker compose installed successfully."
}


# ==========================================
# Install Nginx
# ==========================================

install_nginx() {

    log_info "Installing Nginx..."

    if ! run_remote_command "
        sudo apt install nginx -y
        sudo systemctl enable nginx
        sudo systemctl start nginx
    "
    then
        log_error "Failed to configure nginx"
        exit 1
    fi

    log_info "Nginx installed successfully."
}

# ==========================================
# Verify Installations
# ==========================================

verify_installations() {

    log_info "Verifying installations..."

    if ! run_remote_command "
        git --version
        docker --version
        docker compose version
        nginx -v
    "
    then
        log_error "Installation verification failed."
        exit 1
    fi

    log_info "Verification completed."
}

# ==========================================
# Main
# ==========================================

main() {

    validate_inputs

    wait_for_ssh

    update_server

    install_git
#    install_java
#    install_node
    install_docker
    install_docker_compose
    install_nginx

    verify_installations

    log_info "Bootstrap completed successfully."
}

main