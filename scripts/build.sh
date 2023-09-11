#!/bin/sh

echo "Start building usage-cost-logger"
cd ../usage-cost-logger
mvn clean package
docker build . -t sink-image

echo "Start building usage-cost-processor"
cd ../usage-cost-processor
mvn clean package
docker build . -t processor-image-v2

echo "Start building usage-detail-sender"
cd ../usage-detail-sender
mvn clean package
docker build . -t source-image

echo "Finished"
