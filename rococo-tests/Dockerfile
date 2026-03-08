FROM eclipse-temurin:21-jdk

WORKDIR /rococo
ENV TZ=Europe/Moscow
COPY ./gradle ./gradle
COPY ./rococo-tests ./rococo-tests
COPY ./rococo-grpc ./rococo-grpc
COPY ./gradlew ./
COPY ./build.gradle ./
COPY ./settings.gradle ./
COPY ./gradle.properties ./
COPY ./rococo-tests/entrypoint.sh ./entrypoint.sh

RUN chmod +x ./entrypoint.sh ./gradlew

CMD ["./entrypoint.sh"]
