FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY app/backend/project-service/pom.xml app/backend/project-service/pom.xml
COPY app/backend/project-service/src app/backend/project-service/src
RUN mvn -f app/backend/project-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/app/backend/project-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
