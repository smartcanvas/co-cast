#!/usr/bin/env bash

bash prod.sh
kubectl create -f ../cocast-backend/kubernetes/rc-prd.yaml