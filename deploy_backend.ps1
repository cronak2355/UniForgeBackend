$ErrorActionPreference = "Stop"

# Configuration
$AWS_REGION = "ap-northeast-2"
$ACCOUNT_ID = "859012633338"
$REPO_NAME = "unifor-backend"
$CLUSTER_NAME = "unifor-cluster"
$SERVICE_NAME = "unifor-backend"
$IMAGE_TAG = "latest"
$ECR_URI = "$ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$REPO_NAME"

Write-Host ">>> 1. Building Backend (Gradle)..."
./gradlew clean build -x test
if ($LASTEXITCODE -ne 0) { throw "Gradle Build Failed" }

Write-Host ">>> 2. Logging in to ECR..."
cmd /C "aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com"
if ($LASTEXITCODE -ne 0) { throw "ECR Login Failed" }

Write-Host ">>> 3. Building Docker Image..."
docker build -t "$ECR_URI`:$IMAGE_TAG" .
if ($LASTEXITCODE -ne 0) { throw "Docker Build Failed" }

Write-Host ">>> 4. Pushing Image to ECR..."
docker push "$ECR_URI`:$IMAGE_TAG"
if ($LASTEXITCODE -ne 0) { throw "Docker Push Failed" }

Write-Host ">>> 5. Updating ECS Service (Force New Deployment)..."
aws ecs update-service --cluster $CLUSTER_NAME --service $SERVICE_NAME --force-new-deployment --region $AWS_REGION > $null
if ($LASTEXITCODE -ne 0) { throw "ECS Update Failed" }

Write-Host ">>> Deployment Triggered Successfully!"
