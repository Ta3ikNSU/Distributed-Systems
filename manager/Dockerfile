FROM openjdk:11-jdk-slim AS build
COPY . /manager
WORKDIR /manager
RUN ./gradlew bootJar

FROM openjdk:11-jre-slim
COPY --from=build /manager/build/libs/*.jar manager.jar
CMD ["java", "-jar", "/manager.jar"]