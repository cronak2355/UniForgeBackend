# ALB Security Group
module "alb_sg" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 5.0"

  name        = "${var.project_name}-alb-sg"
  description = "Security group for ALB"
  vpc_id      = module.vpc.vpc_id

  ingress_cidr_blocks = ["0.0.0.0/0"]
  ingress_rules       = ["http-80-tcp", "https-443-tcp"]

  # Allow all egress to send traffic to backend tasks
  egress_rules = ["all-all"]

  tags = var.common_tags
}

# ECS Tasks Security Group
module "ecs_tasks_sg" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 5.0"

  name        = "${var.project_name}-ecs-tasks-sg"
  description = "Security group for ECS Tasks"
  vpc_id      = module.vpc.vpc_id

  # Allow ingress from ALB
  ingress_with_source_security_group_id = [
    {
      rule                     = "http-8080-tcp"
      source_security_group_id = module.alb_sg.security_group_id
    }
  ]

  # Allow egress to Internet (for ECR pull, etc via NAT) and DB/Redis
  egress_rules = ["all-all"]

  tags = var.common_tags
}

# Database Security Group
module "db_sg" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 5.0"

  name        = "${var.project_name}-db-sg"
  description = "Security group for database"
  vpc_id      = module.vpc.vpc_id

  # Allow ingress from ECS Tasks
  ingress_with_source_security_group_id = [
    {
      from_port                = 5432
      to_port                  = 5432
      protocol                 = "tcp"
      description              = "PostgreSQL access from ECS"
      source_security_group_id = module.ecs_tasks_sg.security_group_id
    }
  ]

  # Also allow from VPC CIDR for general access if needed (optional, restrictive is better)
  # Keeping previous CIDR block rule for broader compatibility during dev
  ingress_with_cidr_blocks = [
    {
      from_port   = 5432
      to_port     = 5432
      protocol    = "tcp"
      description = "PostgreSQL access from within VPC"
      cidr_blocks = module.vpc.vpc_cidr_block
    }
  ]

  tags = var.common_tags
}

# Redis Security Group
module "redis_sg" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 5.0"

  name        = "${var.project_name}-redis-sg"
  description = "Security group for Redis"
  vpc_id      = module.vpc.vpc_id

  # Allow ingress from ECS Tasks
  ingress_with_source_security_group_id = [
    {
      from_port                = 6379
      to_port                  = 6379
      protocol                 = "tcp"
      description              = "Redis access from ECS"
      source_security_group_id = module.ecs_tasks_sg.security_group_id
    }
  ]

  # Keeping previous CIDR block rule
  ingress_with_cidr_blocks = [
    {
      from_port   = 6379
      to_port     = 6379
      protocol    = "tcp"
      description = "Redis access from within VPC"
      cidr_blocks = module.vpc.vpc_cidr_block
    },
  ]

  tags = var.common_tags
}
