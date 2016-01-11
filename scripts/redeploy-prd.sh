#!/usr/bin/env bash

bash ./prod.sh
kubectl delete rc cocast-backend-prd
bash ./create-rc-prd.sh
kubectl get rc
kubectl get pods
bash ./dev.sh