#!/bin/bash
source .env

./mvnw clean package
docker build -t hydrum/toonworld:latest ./core
docker push hydrum/toonworld:latest