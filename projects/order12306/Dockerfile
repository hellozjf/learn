FROM openjdk:11.0.3-jdk-stretch
MAINTAINER hellozjf <908686171@qq.com>

# Add the service itself
ARG JAR_FILE
ADD target/${JAR_FILE}  /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]
