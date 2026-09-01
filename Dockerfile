FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

COPY src/main/resources /app/src/main/resources/
COPY target/gcs_dialog.jar /app
ENTRYPOINT ["java", "-jar", "gcs_dialog.jar"]