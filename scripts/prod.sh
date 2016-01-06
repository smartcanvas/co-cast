#!/usr/bin/env bash

gcloud config set container/cluster cocast-cluster-1
gcloud container clusters get-credentials cocast-cluster-1

