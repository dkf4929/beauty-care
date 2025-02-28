FROM openjdk:21
WORKDIR /app

# .jar 파일을 실행하는 명령
CMD ["java", "-jar", "app.jar"]
