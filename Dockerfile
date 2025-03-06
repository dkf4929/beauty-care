FROM openjdk:21

WORKDIR /app

COPY app.jar /app/app.jar

CMD ["java", "-jar", "app.jar"]
