# ==========================================
# Etapa 1: Build
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /workspace

# Copy only pom first to leverage layer caching for dependencies
COPY pom.xml ./

# (optional) download dependencies to cache layers
RUN mvn -B -ntp dependency:go-offline

# Copy source code
COPY src ./src

# Build application
RUN mvn -B -e -DskipTests package


# Runtime stage: lightweight JRE
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy built jar
COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
