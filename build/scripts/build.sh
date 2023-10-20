#!/bin/sh

echo "Start building process-model-creator"
cd ../../sink/process-model-creator
mvn clean package
docker build . -t sink-image

echo "Start building process-mining-data-processor"
cd ../../processor/process-mining-data-processor
mvn clean package
docker build . -t processor-image-v2

echo "Start building sample-data-generator"
cd ../../load-generator/sample-data-generator
mvn clean package
docker build . -t source-image

echo "Start building simple-data-generator"
cd ../../load-generator/simple-data-generator
mvn clean package
docker build . -t source-image-2

echo "Start building filter-processor"
cd ../../processor/filter-processor
mvn clean package
docker build . -t processor-image-2

echo "Start building aggregation-processor"
cd ../../processor/aggregation-processor
mvn clean package
docker build . -t processor-image-3

echo "Start building aggregation-sink"
cd ../../sink/aggregation-sink
mvn clean package
docker build . -t sink-image-2

echo "Finished"