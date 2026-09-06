# ==========================================
# Etapa 1: Build
# ==========================================
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copiar archivos de Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Dar permisos al Maven Wrapper
RUN chmod +x mvnw

# Descargar dependencias
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src src

# Construir aplicación
RUN ./mvnw clean package -DskipTests


# ==========================================
# Etapa 2: Runtime
# ==========================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copiar JAR generado
COPY --from=build /app/target/*.jar app.jar

# Puerto
EXPOSE 8080

# Ejecutar Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]