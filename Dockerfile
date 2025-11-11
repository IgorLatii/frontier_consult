# ====== Build stage ======
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ====== Runtime stage ======
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Define ENV variables (can be redefined at startup)
ENV SPRING_PROFILES_ACTIVE=prod \
    BOT_NAME=frontier_consult_bot \
    BOT_TOKEN="" \
    BOT_OWNER="" \
    MYSQL_HOST="mysql" \
    MYSQL_DATABASE="tg_bot" \
    MYSQL_USER="consult" \
    MYSQL_PASSWORD="StrongPass"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

