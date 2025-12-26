    # 🎮 UniForge Infrastructure

> AWS 기반 게임 제작 플랫폼 인프라 구성

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                           CloudFront CDN                             │
│                         (uniforge.kr + SSL)                          │
└─────────────────────────────────────────────────────────────────────┘
                    │                           │
                    ▼                           ▼
            ┌───────────┐               ┌───────────────┐
            │  S3 Bucket │               │      ALB      │
            │ (Frontend) │               │   (Backend)   │
            └───────────┘               └───────────────┘
                                                │
                                                ▼
                                        ┌───────────────┐
                                        │  ECS Fargate  │
                                        │ (Spring Boot) │
                                        └───────────────┘
                                          │         │
                                          ▼         ▼
                                    ┌─────────┐ ┌─────────┐
                                    │   RDS   │ │  Redis  │
                                    │PostgreSQL│ │ElastiCache│
                                    └─────────┘ └─────────┘
```

## 📁 Terraform Files

| File | Description |
|------|-------------|
| `vpc.tf` | VPC, Subnets, NAT Gateway, S3 Endpoint |
| `compute.tf` | ECS Cluster, Task Definition, ALB, ECR |
| `db.tf` | RDS PostgreSQL, ElastiCache Redis |
| `storage.tf` | S3 Bucket, CloudFront Distribution |
| `security.tf` | Security Groups (ALB, ECS, DB, Redis) |
| `cicd.tf` | GitHub Actions OIDC, IAM Roles |
| `domain.tf` | Route53, ACM Certificate |
| `secrets.tf` | AWS Secrets Manager |
| `variables.tf` | Input Variables |
| `outputs.tf` | Output Values |

## 🌐 Network Configuration

### VPC
- **CIDR**: `10.0.0.0/16`
- **Region**: `ap-northeast-2` (Seoul)

### Subnets
| Type | CIDR | Description |
|------|------|-------------|
| Public | `10.0.101.0/24`, `10.0.102.0/24` | ALB, NAT Gateway |
| Private | `10.0.1.0/24`, `10.0.2.0/24` | ECS Tasks |
| Database | `10.0.201.0/24`, `10.0.202.0/24` | RDS, ElastiCache |

### High Availability
- 2 Availability Zones
- 1 NAT Gateway per AZ
- Multi-AZ RDS & Redis

## 💻 Compute Resources

### ECS Fargate
| Property | Value |
|----------|-------|
| CPU | 256 (0.25 vCPU) |
| Memory | 512 MB |
| Launch Type | FARGATE |
| Desired Count | 1 |

### Application Load Balancer
- HTTP:80 → Target Group (port 8080)
- Health Check: `/actuator/health`

### ECR Repository
- Image Tag: Mutable
- Scan on Push: Enabled

## 🗄️ Database

### RDS PostgreSQL
| Property | Value |
|----------|-------|
| Engine | PostgreSQL 14 |
| Instance | db.t4g.micro |
| Storage | 20GB (auto-scaling to 100GB) |
| Multi-AZ | Enabled |
| Backup | 7 days retention |

### ElastiCache Redis
| Property | Value |
|----------|-------|
| Engine | Redis 7 |
| Node Type | cache.t4g.micro |
| Nodes | 2 (Primary + Replica) |
| Encryption | In-transit & At-rest |
| Multi-AZ | Enabled |

## 📦 Storage & CDN

### S3 Bucket
- Server-Side Encryption: AES256
- Public Access: Blocked
- Access: CloudFront OAC only

### CloudFront Distribution
| Path Pattern | Origin | Cache |
|--------------|--------|-------|
| `/api/*` | ALB | No cache |
| `/oauth2/*` | ALB | No cache |
| `/actuator/*` | ALB | No cache |
| `/*` (default) | S3 | 1 hour |

## 🔐 Security Groups

```
Internet → ALB (80, 443) → ECS (8080) → RDS (5432) / Redis (6379)
```

| Security Group | Inbound | Source |
|----------------|---------|--------|
| ALB | 80, 443 | 0.0.0.0/0 |
| ECS Tasks | 8080 | ALB SG |
| Database | 5432 | ECS SG |
| Redis | 6379 | ECS SG |

## 🌍 Domain & SSL

| Resource | Value |
|----------|-------|
| Domain | `uniforge.kr` |
| DNS | Route53 Hosted Zone |
| SSL | ACM (us-east-1 for CloudFront) |
| Validation | DNS Validation |

## 🚀 CI/CD Pipeline

### GitHub Actions OIDC
- No long-lived credentials stored
- Secure AWS access via web identity federation

### Backend Deployment
```
GitHub Push → Build → Docker Push to ECR → ECS Update Service
```

### Frontend Deployment
```
GitHub Push → Build → S3 Sync → CloudFront Invalidation
```

### IAM Roles
| Role | Permissions |
|------|-------------|
| Backend | ECR Push, ECS Update |
| Frontend | S3 Deploy, CloudFront Invalidation |

## 💰 Estimated Monthly Cost

| Service | Cost |
|---------|------|
| NAT Gateway (2x) | ~$65 |
| RDS Multi-AZ | ~$30 |
| ElastiCache Redis | ~$25 |
| ECS Fargate | ~$10 |
| CloudFront | ~$1 |
| Route53 | ~$0.50 |
| **Total** | **~$130/month** |

## 🔧 Prerequisites

- AWS CLI configured
- Terraform >= 1.0
- Domain registered (Gabia → Route53 NS)

## 📝 Usage

```bash
# Initialize Terraform
terraform init

# Plan changes
terraform plan

# Apply infrastructure
terraform apply

# Destroy infrastructure (caution!)
terraform destroy
```

## 🔑 Required Variables

```hcl
variable "project_name" {
  default = "unifor"
}

variable "domain_name" {
  default = "uniforge.kr"
}

variable "google_client_id" {
  description = "Google OAuth Client ID"
}

variable "google_client_secret" {
  description = "Google OAuth Client Secret"
}
```

## 📚 GitHub Secrets (Required)

| Secret | Description |
|--------|-------------|
| `AWS_ROLE_ARN` | IAM Role ARN for GitHub Actions |
| `CLOUDFRONT_DISTRIBUTION_ID` | CloudFront Distribution ID |
| `VITE_API_URL` | Backend API URL |
| `VITE_GOOGLE_CLIENT_ID` | Google OAuth Client ID |

## 📄 License

MIT License
