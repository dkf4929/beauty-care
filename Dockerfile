FROM openjdk:21
WORKDIR /app

RUN apt-get update && apt-get install -y gradle

# .jar 파일을 실행하는 명령
CMD ["java", "-jar", "app.jar"]
