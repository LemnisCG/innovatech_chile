FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY app/backend/api-gateway/pom.xml app/backend/api-gateway/pom.xml
COPY app/backend/api-gateway/src app/backend/api-gateway/src
RUN mvn -f app/backend/api-gateway/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/app/backend/api-gateway/target/*.jar app.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]
