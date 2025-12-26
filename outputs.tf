output "vpc_id" {
  description = "The ID of the VPC"
  value       = module.vpc.vpc_id
}

output "db_endpoint" {
  description = "The connection endpoint for the RDS DB"
  value       = module.rds.db_instance_endpoint
}

output "redis_endpoint" {
  description = "The connection endpoint for the Redis"
  value       = aws_elasticache_replication_group.redis.primary_endpoint_address
}

output "assets_bucket_name" {
  description = "S3 Bucket Name for Assets"
  value       = aws_s3_bucket.assets.id
}

output "cloudfront_domain" {
  description = "CloudFront Domain Name"
  value       = aws_cloudfront_distribution.cdn.domain_name
}

output "alb_dns_name" {
  description = "The DNS name of the load balancer"
  value       = module.alb.dns_name
}

output "ecr_repository_url" {
  description = "The URL of the ECR repository"
  value       = aws_ecr_repository.backend.repository_url
}

output "cloudfront_distribution_id" {
  value = aws_cloudfront_distribution.cdn.id
}

output "name_servers" {
  description = "The name servers for the Route53 zone"
  value       = aws_route53_zone.main.name_servers
}

# CI/CD Pipeline Outputs
output "github_actions_backend_role_arn" {
  description = "IAM Role ARN for GitHub Actions Backend"
  value       = aws_iam_role.github_actions_backend.arn
}

output "github_actions_frontend_role_arn" {
  description = "IAM Role ARN for GitHub Actions Frontend"
  value       = aws_iam_role.github_actions_frontend.arn
}

output "ecs_cluster_name" {
  description = "ECS Cluster Name"
  value       = aws_ecs_cluster.this.name
}

output "ecs_service_name" {
  description = "ECS Service Name"
  value       = aws_ecs_service.backend.name
}

