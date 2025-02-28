FROM gradle:8.12.1-jdk21 AS build

WORKDIR /app

COPY settings.gradle gradlew gradlew.bat ./

RUN chmod +x gradlew

RUN ./gradlew build --no-daemon -x test || { echo "Gradle build failed! Exiting..."; exit 1; }

COPY . .
RUN ./gradlew clean build --no-daemon || { echo "Gradle clean build failed! Exiting..."; exit 1; }

FROM openjdk:21-jdk

WORKDIR /app

# 빌드한 JAR 파일만 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 실행
CMD ["java", "-jar", "app.jar"]
