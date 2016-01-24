#!/usr/bin/env bash

bash ./dev.sh
kubectl delete pod smartcanvas-teamwork
sleep 5
kubectl create -f ../cocast-sc1-connector/kubernetes/teamwork_dev_pods.yaml
kubectl get pods