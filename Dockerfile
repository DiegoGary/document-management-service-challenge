# syntax=docker/dockerfile:1
FROM maven:3.8.7-amazoncorretto-17 AS build
WORKDIR /workspace

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM amazoncorretto:17 as runtime

ENV JAVA_OPTS="-Xmx50m -Xms50m -XX:MaxRAM=50m -XX:MaxRAMPercentage=95.0"
WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8080
CMD java $JAVA_OPTS -jar app.jar