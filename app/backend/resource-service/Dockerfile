FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY app/backend/resource-service/pom.xml app/backend/resource-service/pom.xml
COPY app/backend/resource-service/src app/backend/resource-service/src
RUN mvn -f app/backend/resource-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/app/backend/resource-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
