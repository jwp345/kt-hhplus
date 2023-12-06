FROM openjdk:17-alpine
ARG JAR_FILE=build/libs/hhplus-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /hhplus.jar

ENTRYPOINT ["java","-jar","/hhplus.jar","-Dspring.profiles.active=prod"]