#!/usr/bin/env bash

# Delete the current pods
kubectl delete rc cocast-backend --cascade=false

# Creates a new one
kubectl create -f ./scripts/replication.yaml