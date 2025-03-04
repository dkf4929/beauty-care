FROM gradle:8.12.1-jdk21 AS build

WORKDIR /app
COPY . .

#RUN gradle clean build --no-daemon
RUN gradle clean build -x test --no-daemon

FROM openjdk:21
WORKDIR /app
COPY --from=build /app/build/libs/beauty-care-0.0.1-SNAPSHOT.jar /app/app.jar

# Step 5: 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]

