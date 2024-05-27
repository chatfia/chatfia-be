# open jdk 17 버전의 환경을 구성
FROM openjdk:17-alpine

# Set the working directory
WORKDIR /app

# build가 되는 시점에 JAR_FILE이라는 변수 명에 build/libs/*.jar 선언
# build/libs - gradle로 빌드했을 때 jar 파일이 생성되는 경로
ARG JAR_FILE=build/libs/*.jar

# JAR_FILE을 app.jar로 복사
COPY ${JAR_FILE} app.jar

## Copy the current directory contents into the container at /app
#COPY target/*.jar app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]

## 운영 및 개발에서 사용되는 환경 설정을 분리
#ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app.jar"]
