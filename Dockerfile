# Build the Spring Boot application with the Java version declared in pom.xml (Java 21).
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests clean package

# Run the packaged application on a smaller JRE image.
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring spring
COPY --from=build --chown=spring:spring /workspace/target/MTDL-SAD-1.0-SNAPSHOT.jar /app/app.jar

USER spring:spring
EXPOSE 8080
EXPOSE 5000/udp
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
