# Deploy Backend to ECS

$REGION = "ap-northeast-2"
$ACCOUNT_ID = "859012633338"
$ECR_REPO = "$ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/unifor-backend"
$CLUSTER_NAME = "unifor-cluster"
$SERVICE_NAME = "unifor-backend"

Write-Host "1. Building Docker Image..."
docker build -t unifor-backend ./backend
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "2. Logging into ECR..."
aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin "$ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "3. Tagging Image..."
docker tag unifor-backend:latest "$ECR_REPO:latest"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "4. Pushing Image to ECR..."
docker push "$ECR_REPO:latest"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "5. Updating ECS Service..."
aws ecs update-service --cluster $CLUSTER_NAME --service $SERVICE_NAME --force-new-deployment
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "Deployment Triggered Successfully!"
