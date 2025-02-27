FROM gradle:8.12.1-jdk21 AS build

WORKDIR /app
COPY . .

RUN gradle clean test --no-daemon -Dspring.profiles.active=test

FROM openjdk:21
WORKDIR /app
COPY --from=build /app/build/libs/beauty-care-0.0.1-SNAPSHOT.jar /app/app.jar

CMD ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]