FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:17-jre-focal
WORKDIR /app
COPY --from=builder /app/target/OtpService_PJ-1.0-SNAPSHOT.jar .
EXPOSE 8080
CMD ["java", "-jar", "OtpService_PJ-1.0-SNAPSHOT.jar"]
