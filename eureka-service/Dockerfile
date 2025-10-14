# ---------- Etapa 1: Build con Maven ----------
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copiamos pom.xml para cachear dependencias
COPY pom.xml .

# Descargamos dependencias offline
RUN mvn dependency:go-offline -B

# Copiamos el código fuente
COPY src ./src

# Construimos el JAR (omitimos tests para acelerar)
RUN mvn clean package -DskipTests

# ---------- Etapa 2: Imagen final ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiamos el JAR desde la etapa de build
COPY --from=builder /app/target/*.jar app.jar

# Variables de entorno (opcional)
ENV JAVA_OPTS=""

# Exponemos el puerto de Eureka
EXPOSE 8761

# Comando de ejecución
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
