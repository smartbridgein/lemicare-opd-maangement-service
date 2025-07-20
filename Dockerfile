FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy jar file
COPY target/opd-management-service-0.0.1-SNAPSHOT.jar app.jar

# Hard-code port 8084 for Cloud Run
ENV PORT=8084
ENV SPRING_PROFILES_ACTIVE=cloud

# Expose port 8084
EXPOSE 8084

# Run with explicit debug settings
CMD ["java", "-Dserver.port=8084", "-Dspring.profiles.active=cloud", "-XX:InitialRAMPercentage=50", "-XX:MaxRAMPercentage=70", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
