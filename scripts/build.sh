#!/bin/sh

echo "Start building process-model-creator"
cd ../process-model-creator
mvn clean package
docker build . -t sink-image

echo "Start building process-mining-data-processor"
cd ../process-mining-data-processor
mvn clean package
docker build . -t processor-image-v2

echo "Start building sample-data-generator"
cd ../sample-data-generator
mvn clean package
docker build . -t source-image

echo "Finished"
