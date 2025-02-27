FROM gradle:8.12.1-jdk21 AS build

WORKDIR /app
COPY . .

# 테스트는 DB 연결 문제로 컨테이너가 healthy 상태일 때, 테스트한다.
RUN gradle clean build -x test --no-daemon

FROM openjdk:21
WORKDIR /app
COPY --from=build /app/build/libs/beauty-care-0.0.1-SNAPSHOT.jar /app/app.jar

CMD ["java", "-jar", "app.jar"]