FROM openjdk:21
WORKDIR /app

RUN curl -s https://services.gradle.org/distributions/gradle-8.12.1-bin.zip -o gradle.zip \
    && unzip gradle.zip -d /opt/gradle \
    && rm gradle.zip \
    && ln -s /opt/gradle/gradle-8.12.1/bin/gradle /usr/local/bin/gradle

# .jar 파일을 실행하는 명령
CMD ["java", "-jar", "app.jar"]
