# =============================================================================
# 일반 설정
# =============================================================================
variable "aws_region" {
  description = "AWS 리전"
  type        = string
  default     = "ap-northeast-2"
}

variable "environment" {
  description = "환경 (dev, staging, prod)"
  type        = string
  default     = "prod"
}

variable "project_name" {
  description = "프로젝트 이름"
  type        = string
  default     = "unifor"
}

# =============================================================================
# VPC 설정
# =============================================================================
variable "vpc_cidr" {
  description = "VPC CIDR 블록"
  type        = string
  default     = "10.0.0.0/16"
}

variable "availability_zones" {
  description = "사용할 가용 영역"
  type        = list(string)
  default     = ["ap-northeast-2a", "ap-northeast-2b", "ap-northeast-2c"]
}

variable "public_subnet_cidrs" {
  description = "Public 서브넷 CIDR"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
}

variable "private_subnet_cidrs" {
  description = "Private 서브넷 CIDR"
  type        = list(string)
  default     = ["10.0.11.0/24", "10.0.12.0/24", "10.0.13.0/24"]
}

# =============================================================================
# ECS 설정
# =============================================================================
variable "ecs_cluster_name" {
  description = "ECS 클러스터 이름"
  type        = string
  default     = "unifor-cluster"
}

variable "backend_service_name" {
  description = "백엔드 ECS 서비스 이름"
  type        = string
  default     = "unifor-backend"
}

variable "backend_container_port" {
  description = "백엔드 컨테이너 포트"
  type        = number
  default     = 8080
}

variable "backend_cpu" {
  description = "백엔드 CPU 단위"
  type        = number
  default     = 2048
}

variable "backend_memory" {
  description = "백엔드 메모리 (MB)"
  type        = number
  default     = 4096
}

variable "backend_desired_count" {
  description = "백엔드 서비스 인스턴스 수"
  type        = number
  default     = 2
}

variable "backend_min_count" {
  description = "Auto Scaling 최소 태스크 수"
  type        = number
  default     = 2
}

variable "backend_max_count" {
  description = "Auto Scaling 최대 태스크 수"
  type        = number
  default     = 10
}

variable "backend_cpu_threshold" {
  description = "CPU 스케일링 임계값 (%)"
  type        = number
  default     = 70
}

variable "backend_memory_threshold" {
  description = "메모리 스케일링 임계값 (%)"
  type        = number
  default     = 80
}

variable "backend_requests_per_target" {
  description = "타겟당 요청 수 임계값"
  type        = number
  default     = 1000
}

# =============================================================================
# RDS 설정
# =============================================================================
variable "db_instance_identifier" {
  description = "RDS 인스턴스 식별자"
  type        = string
  default     = "unifor-db"
}

variable "db_instance_class" {
  description = "RDS 인스턴스 클래스"
  type        = string
  default     = "db.t3.medium"
}

variable "db_name" {
  description = "데이터베이스 이름"
  type        = string
  default     = "unifor"
}

variable "db_username" {
  description = "데이터베이스 사용자명"
  type        = string
  sensitive   = true
}

variable "db_password" {
  description = "데이터베이스 비밀번호"
  type        = string
  sensitive   = true
}

variable "db_allocated_storage" {
  description = "RDS 스토리지 크기 (GB)"
  type        = number
  default     = 20
}

variable "db_read_replica_count" {
  description = "RDS Read Replica 수"
  type        = number
  default     = 1
}

variable "db_replica_instance_class" {
  description = "RDS Read Replica 인스턴스 클래스"
  type        = string
  default     = "db.t3.medium"
}

# =============================================================================
# S3 설정
# =============================================================================
variable "assets_bucket_name" {
  description = "에셋 저장용 S3 버킷 이름"
  type        = string
  default     = "uniforge-assets"
}

variable "generated_assets_bucket_name" {
  description = "생성된 에셋 저장용 S3 버킷 이름"
  type        = string
  default     = "generated-assets"
}

# =============================================================================
# ElastiCache Redis 설정
# =============================================================================
variable "redis_node_type" {
  description = "ElastiCache Redis 노드 타입"
  type        = string
  default     = "cache.t3.medium"
}

variable "redis_num_cache_clusters" {
  description = "Redis 캐시 클러스터 수 (2 이상이면 자동 장애 조치 활성화)"
  type        = number
  default     = 2
}

variable "redis_engine_version" {
  description = "Redis 엔진 버전"
  type        = string
  default     = "7.0"
}

variable "redis_auth_token" {
  description = "Redis AUTH 토큰 (비밀번호)"
  type        = string
  sensitive   = true
  default     = ""
}

variable "redis_sns_topic_arn" {
  description = "Redis 알림용 SNS 토픽 ARN"
  type        = string
  default     = ""
}

# =============================================================================
# CloudFront 설정
# =============================================================================
variable "cloudfront_price_class" {
  description = "CloudFront 가격 등급"
  type        = string
  default     = "PriceClass_200"
}

# =============================================================================
# ECR 설정
# =============================================================================
variable "ecr_repositories" {
  description = "ECR 리포지토리 목록"
  type        = list(string)
  default     = ["unifor-backend", "uni-server"]
}

# =============================================================================
# ACM 설정
# =============================================================================
variable "acm_certificate_arn" {
  description = "ALB HTTPS용 ACM 인증서 ARN"
  type        = string
  default     = "" # 실제 인증서 ARN으로 설정 필요
}
