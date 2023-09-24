#!/bin/sh

echo "Start building process-model-creator"
cd ../process-model-creator
mvn clean package
docker build --platform linux/amd64 . -t harbor.se.internal/hre/process-model-creator:latest

echo "Start building process-mining-data-processor"
cd ../process-mining-data-processor
mvn clean package
docker build --platform linux/amd64 . -t harbor.se.internal/hre/process-mining-data-processor:latest

echo "Start building sample-data-generator"
cd ../sample-data-generator
mvn clean package
docker build --platform linux/amd64 . -t harbor.se.internal/hre/sample-data-generator:latest

echo "Finished"