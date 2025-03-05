FROM gradle:8.12.1-jdk21 AS build

WORKDIR /app
COPY . .

ENV TESTCONTAINERS_HOST_OVERRIDE=host.docker.internal
STOPSIGNAL SIGKILL
VOLUME /var/run/docker.sock:/var/run/docker.sock

RUN gradle clean build --no-daemon --info
#RUN gradle clean build -x test --no-daemon


FROM openjdk:21
WORKDIR /app
COPY --from=build /app/build/libs/beauty-care-0.0.1-SNAPSHOT.jar /app/app.jar

# Step 5: 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]

