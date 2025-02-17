# Step 1: Gradle 빌드 환경을 설정한 이미지 사용
FROM gradle:8.12.1-jdk21 AS build

# Step 2: 애플리케이션 소스 코드 복사
WORKDIR /app
COPY . .

# Step 3: Gradle 빌드 실행
RUN gradle clean build --no-daemon

# Step 4: 빌드된 애플리케이션을 배포 이미지로 복사
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/beauty-care-0.0.1-SNAPSHOT.jar /app/app.jar

# Step 5: 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]
