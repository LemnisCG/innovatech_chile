FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY app/backend/analytics-service/pom.xml app/backend/analytics-service/pom.xml
COPY app/backend/analytics-service/src app/backend/analytics-service/src
RUN mvn -f app/backend/analytics-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/app/backend/analytics-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
