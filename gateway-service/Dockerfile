# Etapa 1: build con Maven
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copiamos el descriptor primero para aprovechar cache
COPY pom.xml .

# Descargamos dependencias (cachea esta capa)
RUN mvn dependency:go-offline -B

# Copiamos el resto del código fuente
COPY src ./src

# Construimos el JAR (omitimos tests para acelerar)
RUN mvn clean package -Dmaven.test.skip=true

# Etapa 2: imagen final con JRE (más liviana)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiamos solo el JAR compilado desde la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Configuración de entorno
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS=""

# Exponemos el puerto interno
EXPOSE 8081

# Comando de ejecución
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
