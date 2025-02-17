# Base image 설정
FROM openjdk:21-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 애플리케이션의 JAR 파일을 컨테이너로 복사
COPY ./build/libs/beauty-care-app.jar /app/app.jar

# 애플리케이션 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
