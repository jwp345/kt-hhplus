FROM openjdk:17-alpine

# 8080 포트로 배포
EXPOSE 8080

# 작업 공간 이동
WORKDIR /app
ARG JAR_FILE=build/libs/hhplus-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /hhplus.jar

ENTRYPOINT ["java","-jar","/hhplus.jar"]