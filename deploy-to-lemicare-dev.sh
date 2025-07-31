#!/bin/bash
# Cloud Run deployment script for opd-management-service
# Target project: lemicare-prod

# Define variables
PROJECT_ID="lemicaredev"
REGION="asia-south1"
SERVICE_NAME="opd-management-service"
IMAGE="gcr.io/${PROJECT_ID}/${SERVICE_NAME}:latest"

echo "=== Building ${SERVICE_NAME} JAR file ===" 
# Build the Java application with Maven
mvn clean package

# Check if the build was successful
if [ $? -ne 0 ] || [ ! -f "target/opd-management-service-0.0.1-SNAPSHOT.jar" ]; then
  echo "Maven build failed or JAR file not found. Aborting deployment."
  exit 1
fi

echo "=== Building Docker image using Cloud Build ===" 
# Use Cloud Build to build the Docker image from the existing Dockerfile
gcloud builds submit --tag="${IMAGE}" --project="${PROJECT_ID}" .

if [ $? -eq 0 ]; then
  echo "=== Deploying to Cloud Run ===" 
  gcloud run deploy "${SERVICE_NAME}" \
    --image="${IMAGE}" \
    --platform=managed \
    --region="${REGION}" \
    --allow-unauthenticated \
    --set-env-vars="SPRING_PROFILES_ACTIVE=cloud" \
    --set-env-vars="ALLOWED_ORIGINS=https://healthcare-app-191932434541.asia-south1.run.app" \
    --port=8084 \
    --memory=512Mi \
    --project="${PROJECT_ID}"
  
  if [ $? -eq 0 ]; then
    echo "=== Deployment completed successfully! ===" 
    echo "Your ${SERVICE_NAME} is now available. Check the URL in the output above."
  else
    echo "=== Deployment to Cloud Run failed ===" 
    exit 1
  fi
else
  echo "=== Docker image build failed ===" 
  exit 1
fi
