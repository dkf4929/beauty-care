FROM openjdk:21
WORKDIR /app
RUN apk update && apk add gradle

# .jar 파일을 실행하는 명령
CMD ["java", "-jar", "app.jar"]
