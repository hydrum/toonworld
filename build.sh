#!/bin/bash

set -e # Exit immediately if a command exits with a non-zero status
set -o pipefail

source .env

./mvnw clean verify package
docker build -t hydrum/toonworld:latest ./core
docker push hydrum/toonworld:latest