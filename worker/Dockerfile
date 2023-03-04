FROM openjdk:11-jdk-slim AS build
WORKDIR /worker
COPY . /worker
RUN ./gradlew build

WORKDIR /

COPY "/worker/build/libs/worker.jar" "worker.jar"
ENTRYPOINT ["java","-jar","/worker.jar"]