# 🚀 AI Cloud Cost Optimizer


<p align="center">
  <img src="https://skillicons.dev/icons?i=java,spring,react,postgres,redis,docker,aws,github,maven,linux,bash" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20AI-AI%20Powered-6DB33F?style=flat-square" />
  <img src="https://img.shields.io/badge/RAG-Retrieval%20Augmented%20Generation-blue?style=flat-square" />
  <img src="https://img.shields.io/badge/OAuth2-Google%20%26%20GitHub-success?style=flat-square" />
  <img src="https://img.shields.io/badge/JWT-Authentication-black?style=flat-square" />
  <img src="https://img.shields.io/badge/Redis-Caching-red?style=flat-square" />
</p>

An AI-powered cloud optimization platform built using Java Spring Boot, React, AWS, Redis, and Docker that helps organizations discover cloud resources, analyze utilization patterns, identify optimization opportunities, and generate intelligent recommendations for reducing cloud costs.

The project demonstrates end-to-end Backend Development, Cloud Engineering, AI Integration, and DevOps Automation.

---

# 📸 Application Screenshots

## Dashboard

![Dashboard](docs/images/dashboard.png)

## Resource Explorer

![Resource Explorer](docs/images/resource-explorer.png)

## Optimization Center

![Optimization Center](docs/images/optimization-center.png)

## AI Recommendation Engine

![AI Recommendation](docs/images/ai-recommendation.png)

## Reports

![Reports](docs/images/reports.png)

---

# ✨ Key Highlights

### AI-Powered Cloud Optimization

* Resource Discovery
* Resource Analysis
* AI Recommendation Generation
* Optimization Reporting

### Cloud Resource Management

* AWS Account Integration
* EC2 Discovery
* S3 Discovery
* RDS Discovery
* EKS Discovery

### Security

* JWT Authentication
* Google OAuth Login
* GitHub OAuth Login
* Spring Security

### Performance

* Redis Caching Layer
* Reduced AWS API Calls
* Faster Dashboard Experience

### DevOps

* Dockerized Deployment
* Automated AWS Infrastructure Provisioning
* Fully Scripted Environment Setup
* Zero Manual Server Configuration

---

# 🏗 Architecture

```text
                 ┌────────────────────┐
                 │     React UI       │
                 └─────────┬──────────┘
                           │
                           ▼
                 ┌────────────────────┐
                 │ Spring Boot API    │
                 └─────────┬──────────┘
                           │
          ┌────────────────┼──────────────┐
          ▼                ▼              ▼

     PostgreSQL        Redis Cache     AWS SDK

                                           │
                                           ▼

                      EC2 | S3 | RDS | EKS | CloudWatch
```

---

# 🤖 AI Optimization Workflow

```text
AWS Account
      │
      ▼
Resource Discovery
      │
      ▼
Metrics Collection
      │
      ▼
Resource Analysis Engine
      │
      ▼
AI Recommendation Service
      │
      ▼
Optimization Report
```

---

# ⚙️ Tech Stack

## Backend

* Java 17
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* OAuth2
* JWT

## Frontend

* React
* Vite
* Tailwind CSS

## Database

* PostgreSQL

## Cache

* Redis

## Cloud

* AWS EC2
* AWS IAM
* AWS CloudWatch
* AWS SDK

## DevOps

* Docker
* Docker Compose
* Shell Scripting

---

# 🚀 Fully Automated AWS Deployment

One of the major highlights of this project is the complete automation of infrastructure provisioning and deployment.

The entire 3-tier application is deployed on AWS EC2 using only Shell Scripts.

No manual server setup is required.

---

## Deployment Pipeline

```text
launch.sh
    │
    ▼
bootstrap-server.sh
    │
    ▼
deploy-container.sh
    │
    ▼
Application Ready
```

---

## launch.sh

Automates:

* EC2 Provisioning
* Security Group Creation
* Key Pair Management
* Instance Launch

---

## bootstrap-server.sh

Automates:

* OS Updates
* Docker Installation
* Docker Compose Installation
* Environment Preparation
* Runtime Configuration

---

## deploy-container.sh

Automates:

* Docker Image Pull
* Container Creation
* Network Setup
* Application Deployment
* Health Validation

---

# ⚡ Redis Caching Strategy

Cached Resources:

* Resource Inventory
* Resource Metrics
* EC2 Details
* S3 Details
* RDS Details
* EKS Details

Benefits:

* Faster Dashboard Loading
* Reduced AWS API Requests
* Improved User Experience
* Lower Latency

---

# 🔐 Authentication

### Traditional Authentication

* Registration
* Login
* JWT Authorization

### OAuth2 Authentication

* Google Login
* GitHub Login

---

# 📊 Features

### Resource Explorer

* View Resources
* Resource Details
* Metrics Visualization

### Optimization Center

* Resource Analysis
* Optimization Suggestions
* AI Recommendations

### Reports

* Historical Reports
* Optimization Findings
* Resource Tracking

---

# 🎯 What This Project Demonstrates

### Java Backend Engineering

* Spring Boot
* Security
* REST APIs
* JPA/Hibernate
* Redis

### AI Integration

* Recommendation Engine
* Resource Analysis
* Intelligent Optimization Suggestions

### Cloud Engineering

* AWS Integration
* CloudWatch Metrics
* Resource Discovery

### DevOps Engineering

* Docker
* Infrastructure Automation
* Shell Scripting
* Deployment Automation
* Production Hosting

