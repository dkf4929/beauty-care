# 빌드 단계
FROM gradle:8.12.1-jdk21 AS build

WORKDIR /app

COPY build.gradle settings.gradle ./

RUN gradle clean build --no-daemon

COPY . .

FROM openjdk:21

WORKDIR /app

COPY --from=build /app/build/libs/beauty-care-0.0.1-SNAPSHOT.jar /app/app.jar

# 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]