# 1단계: Gradle 빌드 컨테이너
FROM gradle:8.12.1-jdk21 AS build

WORKDIR /app

# Gradle 관련 파일만 먼저 복사하여 Gradle 캐시 활용
COPY build.gradle settings.gradle gradlew gradlew.bat ./

# 실행 권한 부여
RUN chmod +x gradlew

# 의존성 다운로드 (실패 시 즉시 종료)
RUN ./gradlew build --no-daemon -x test || { echo "Gradle build failed! Exiting..."; exit 1; }

# 프로젝트 전체 복사 후 빌드 (실패 시 즉시 종료)
COPY . .
RUN ./gradlew clean build --no-daemon || { echo "Gradle clean build failed! Exiting..."; exit 1; }

# 2단계: 실행 컨테이너 (최종 컨테이너)
FROM openjdk:21-jdk

WORKDIR /app

# 빌드한 JAR 파일만 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 실행
CMD ["java", "-jar", "app.jar"]
