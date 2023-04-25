FROM openjdk:11-jdk-slim AS build
WORKDIR /worker
COPY . /worker
RUN ./gradlew bootJar
WORKDIR /
ENTRYPOINT ["java","-jar","/worker/build/libs/worker.jar"]