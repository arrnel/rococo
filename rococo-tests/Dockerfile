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

CMD ./gradlew test -Dtest.env=docker -Duser.timezone=Europe/Moscow
