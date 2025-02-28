FROM gradle:8.12.1-jdk21 AS build

WORKDIR /app
COPY . .

RUN apt-get update && apt-get install -y mysql-client && \
    until mysql -h 52.79.55.156 -u root -pqwer1234 -e "SELECT 1;" ; do \
        echo "Waiting for MySQL to be ready..."; sleep 3; \
    done

RUN gradle clean build --no-daemon

FROM openjdk:21
WORKDIR /app
COPY --from=build /app/build/libs/beauty-care-0.0.1-SNAPSHOT.jar /app/app.jar

CMD ["java", "-jar", "app.jar"]