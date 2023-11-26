#!/bin/sh

cd ../..
mvn clean install -DskipTests=true

echo "Start building simple-data-generator"
cd load-generator/simple-data-generator
docker build --platform linux/amd64 . -t harbor.se.internal/hre/simple-data-generator:latest

echo "Start building filter-processor"
cd ../../processor/filter-processor
docker build --platform linux/amd64 . -t harbor.se.internal/hre/filter-processor:latest

echo "Start building aggregation-processor"
cd ../../processor/aggregation-processor
docker build --platform linux/amd64 . -t harbor.se.internal/hre/aggregation-processor:latest

echo "Start building aggregation-sink"
cd ../../sink/aggregation-sink
docker build --platform linux/amd64 . -t harbor.se.internal/hre/aggregation-sink:latest

echo "Start building filter-sink"
cd ../../sink/filter-sink
docker build --platform linux/amd64 . -t harbor.se.internal/hre/filter-sink:latest

echo "Start building burattin-sink"
cd ../../sink/burattin-sink
docker build --platform linux/amd64 . -t harbor.se.internal/hre/burattin-sink:latest

echo "Start building precision-checker"
cd ../../sink/precision-checker
docker build --platform linux/amd64 . -t harbor.se.internal/hre/precision-checker:latest

echo "Finished"