FROM openjdk:11-jdk-slim AS build
WORKDIR /manager
COPY . /manager
RUN ./gradlew bootJar
WORKDIR /
ENTRYPOINT ["java","-jar","/manager/build/libs/manager.jar"]