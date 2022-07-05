# TODO: Shift to eclipse termurin openjdk are no longer maintaining their builds.
# See deprecation notice here : https://hub.docker.com/_/openjdk
FROM openjdk:11.0.13-jre-slim

COPY target/*.jar app.jar

## Default remote-debug on
CMD ["java", "-jar", "/app.jar"]