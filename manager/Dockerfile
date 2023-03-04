FROM openjdk:11-jdk-slim AS build
WORKDIR /manager
COPY . /manager
RUN ./gradlew bootJar

WORKDIR /

COPY "/manager/build/libs/manager.jar" "manager.jar"
ENTRYPOINT ["java","-jar","/manager.jar"]