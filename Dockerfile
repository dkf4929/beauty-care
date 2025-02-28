FROM openjdk:21
WORKDIR /app
RUN curl -s https://get.sdkman.io | bash
RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk install gradle"

# .jar 파일을 실행하는 명령
CMD ["java", "-jar", "app.jar"]
