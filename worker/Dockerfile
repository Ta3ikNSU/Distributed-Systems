FROM openjdk:11-jdk-slim AS build
COPY . /worker
WORKDIR /worker
RUN ./gradlew build

FROM openjdk:11-jre-slim
COPY --from=build /worker/build/libs/*.jar worker.jar
CMD ["java", "-jar", "/worker.jar"]