#!/usr/bin/env bash

gcloud config set container/cluster cocast-dev
gcloud container clusters get-credentials cocast-dev
