FROM eclipse-temurin:11
COPY target/aggregation-processor-0.0.1-SNAPSHOT.jar app.jar
ADD target/libs libs/
ENTRYPOINT ["java","-jar","/app.jar"]
