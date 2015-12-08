#!/usr/bin/env bash

# Setup GCP project
# I'm assuming here that the project's name is co-cast
gcloud config set project co-cast

# Setup GCP default zone
gcloud config set compute/zone us-central1-b

# Setup the default container / cluster
# Please, create the container cluster first using the Cloud Console
# More information here: https://cloud.google.com/container-engine/docs/clusters/operations
gcloud config set container/cluster cocast-dev

# Get credentials for kubectl
gcloud container clusters get-credentials cocast-dev

# View configuration
gcloud config list

# Expose the container as a service on port 8080
kubectl create -f ../cocast-backend/kubernetes/services-dev.yaml
kubectl create -f ../cocast-backend/kubernetes/services-prd.yaml

# List the services to get the IP
kubectl get services
echo Repeat the command \'kubectl get services\' in a few minutes to get the external IP... it takes a while
