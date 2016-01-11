#!/usr/bin/env bash

bash ./dev.sh
kubectl delete rc cocast-backend-dev
bash ./create-rc-dev.sh
kubectl get rc
kubectl get pods