# Multi-stage Dockerfile for agent-core (Java 21)
# Build stage: use Maven with Eclipse Temurin 21 JDK
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# copy only Maven files first to leverage layer caching
COPY pom.xml mvnw* ./
COPY .mvn .mvn

# copy source and build
COPY src ./src
RUN mvn -B -e -DskipTests package

# Runtime stage: lightweight JRE for Java 21 (Eclipse Temurin)
FROM eclipse-temurin:21-jre-jammy

ARG JAR_FILE=target/agent-core-0.0.1-SNAPSHOT.jar
WORKDIR /app

# Copy the fat/packaged jar from the build stage
COPY --from=build /workspace/${JAR_FILE} /app/app.jar

# Recommended non-root user (optional). Render runs containers as root by default,
# but it's a good practice to create a user. If you prefer root, remove the following.
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser || true
USER appuser

EXPOSE 8080

# JVM tuning via env var
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

