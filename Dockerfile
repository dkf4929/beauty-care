FROM gradle:8.12.1-jdk21 AS build

WORKDIR /app
COPY . .

RUN gradlew clean build

FROM openjdk:21
WORKDIR /app
COPY --from=build /app/build/libs/beauty-care-0.0.1-SNAPSHOT.jar /app/app.jar

CMD ["java", "-jar", "app.jar"]