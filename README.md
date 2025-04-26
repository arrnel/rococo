<br>
<img src="/images/logo.png" alt="logo" width="300"/>

# Содержание

* [Использованные технологии](#использованные-технологии)
* [Схема проекта Rococo](#схема-проекта-rococo)
* [Сетевые порты и их назначение](#сетевые-порты-и-их-назначение)
* [Минимальные предусловия для работы с проектом Rococo](#минимальные-предусловия-для-работы-с-проектом-rococo)
* [Локальный запуск Rococo в среде разработки](#запуск-rococo-локально-в-ide)
* [Развёртывание Rococo в Docker](#развёртывание-rococo-в-docker)
* [Запуск тестов в Docker](#запуск-тестов-в-docker)
* [Образец отчёта о тестировании](#образец-отчёта-о-тестировании)

# Использованные технологии

- [Spring Authorization Server](https://spring.io/projects/spring-authorization-server)
- [Spring OAuth 2.0 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
- [Spring data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Web](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#spring-web)
- [Spring actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Spring gRPC](https://yidongnan.github.io/grpc-spring-boot-starter/en/server/getting-started.html)
- [Spring web-services](https://docs.spring.io/spring-ws/docs/current/reference/html/)
- [Apache Kafka](https://developer.confluent.io/quickstart/kafka-docker/)
- [Docker](https://www.docker.com/resources/what-container/)
- [Docker-compose](https://docs.docker.com/compose/)
- [Postgres](https://www.postgresql.org/about/)
- [React](https://ru.reactjs.org/docs/getting-started.html)
- [GraphQL](https://graphql.org/)
- [Thymeleaf](https://www.thymeleaf.org/)
- [Jakarta Bean Validation](https://beanvalidation.org/)
- [JUnit 5 (Extensions, Resolvers, etc)](https://junit.org/junit5/docs/current/user-guide/)
- [Retrofit 2](https://square.github.io/retrofit/)
- [Allure](https://docs.qameta.io/allure/)
- [Selenide](https://selenide.org/)
- [Selenoid & Selenoid-UI](https://aerokube.com/selenoid/latest/)
- [Allure-docker-service](https://github.com/fescobar/allure-docker-service)
- [Java 21](https://adoptium.net/en-GB/temurin/releases/)
- [Gradle 8.6](https://docs.gradle.org/8.6/release-notes.html)
- [GHA](https://docs.github.com/en/actions)
- И многие другие

# Схема проекта Rococo

<img src="/images/services.png" alt="services" width="800"/>

# Сетевые порты и их назначение

| Сервис    | Порт | Описание сервиса                                        |
|-----------|------|---------------------------------------------------------|
| FRONTEND  | 3000 | Клиент                                                  |
| AUTH      | 9000 | Необходим для регистрации, аутентификации и авторизации |
| GATEWAY   | 9001 | Маршрутизирующий сервис                                 |
| ARTISTS   | 9002 | Хранит информацию о художниках                          |
| COUNTRIES | 9003 | Хранит информацию о художниках                          |
| FILES     | 9004 | Хранит информацию об изображениях сущностей             |
| MUSEUMS   | 9005 | Хранит информацию о музеях                              |
| PAINTINGS | 9006 | Хранит информацию о картинах                            |
| USERS     | 9007 | Хранит информацию о пользователях                       |

Прочее (при запуске в Docker)

| Сервис      | Порт |
|-------------|------|
| FRONTEND    | 8080 |
| ALLURE      | 5050 |
| ALLURE-UI   | 5051 |
| SELENOID    | 4444 |
| SELENOID-UI | 5052 |

При локальном запуске через docker-compose.local.yml или docker-compose.local-mock.yml можно конфигурировать порты
приложения.
В иных случаях конфигурация не обязательна, так как все существующие контейнеры удаляются и используются значения по
умолчанию.

<details>
<summary>Полный список используемых переменных окружений в локальной среде</summary>

Если у вас операционная система на базе Linux, то переменные окружения лучше сохранять в /etc/environment.
Так переменные окружения будут присутствовать в переменных окружения ОС в IDEA.

### !!! Для запуска тестов необходимо сгенерировать Fine-grained personal access token и его наименовнание.

Сгенерировать Fine-grained personal access token ключ по ссылке: https://github.com/settings/personal-access-tokens

<span style = "color:#ff001e"> * Обязательные переменные и проперти</span>

Переменные окружения:

| Ключ                    | Значение        |
|-------------------------|-----------------|
| * GITHUB_TOKEN_NAME     | _**Имя PAT**_   |
| * GITHUB_TOKEN          | _**Сам токен**_ |
| ROCOCO_DB_PORT          | 5432            |
| ROCOCO_DB_USER          | postgres        |
| ROCOCO_DB_PASSWORD      | secret          |
| ROCOCO_PGADMIN_PORT     | 5433            |
| ROCOCO_PGADMIN_EMAIL    | mymail@mail.ru  |
| ROCOCO_PGADMIN_PASSWORD | 12345           |
| ROCOCO_KAFKA_PORT       | 9092            |
| ROCOCO_ZOOKEEPER_PORT   | 2181            |
| ROCOCO_FRONT_PORT       | 3000            |
| ROCOCO_WIREMOCK_PORT    | 9001            |
| ROCOCO_AUTH_PORT        | 9000            |
| ROCOCO_GATEWAY_PORT     | 9001            |
| ROCOCO_ARTISTS_PORT     | 9002            |
| ROCOCO_COUNTRIES_PORT   | 9003            |
| ROCOCO_FILES_PORT       | 9004            |
| ROCOCO_MUSEUMS_PORT     | 9005            |
| ROCOCO_PAINTINGS_PORT   | 9006            |
| ROCOCO_USERS_PORT       | 9007            |

| Ключ       | Значение |
|------------|----------|
| * test.env | local    |

</details>

# Минимальные предусловия для работы с проектом Rococo

#### 0. Если у вас ОС Windows

Во-первых, и в-главных, необходимо использовать [bash terminal](https://www.geeksforgeeks.org/working-on-git-bash/), а
не powershell.
[Полезное и короткое видео о git bash](https://www.youtube.com/watch?v=zM9Mb-otqww)
Обязательно добавьте bash терминал
в [качестве терминала в вашей IDE (IDEA, PyCharm)](https://stackoverflow.com/questions/20573213/embed-git-bash-in-pycharm-as-external-tool-and-work-with-it-in-pycharm-window-w)
Во-вторых, если у вас что-то не работает - пишите в TG чат группы - будем вместе дополнять README, т.к. изначально
проект разработан под nix

#### 1. Установить docker (Если не установлен)

Мы будем использовать docker для БД (Postgres), кроме того, будем запускать микросервисы в едином docker network при
помощи docker-compose

[Установка на Windows](https://docs.docker.com/desktop/install/windows-install/)

[Установка на Mac](https://docs.docker.com/desktop/install/mac-install/) (Для ARM и Intel разные пакеты)

[Установка на Linux](https://docs.docker.com/desktop/install/linux-install/)

После установки и запуска docker daemon необходимо убедиться в работе команд docker, например `docker -v`:

```posh
User-MacBook-Pro ~ % docker -v
Docker version 20.10.14, build a224086
```

#### 2. Спуллить контейнер postgres:15.1, zookeeper и kafka версии 7.3.2

```posh
docker pull postgres:15.1
docker pull confluentinc/cp-zookeeper:7.3.2
docker pull confluentinc/cp-kafka:7.3.2
```

После `pull` вы увидите спуленный image командой `docker images`

```posh
mitriis-MacBook-Pro ~ % docker images            
REPOSITORY                 TAG              IMAGE ID       CREATED         SIZE
postgres                   15.1             9f3ec01f884d   10 days ago     379MB
confluentinc/cp-kafka      7.3.2            db97697f6e28   12 months ago   457MB
confluentinc/cp-zookeeper  7.3.2            6fe5551964f5   7 years ago     451MB

```

#### 3. Создать volume для сохранения данных из БД в docker на вашем компьютере

```posh
docker volume create pgdata
```

#### 4. Запустить БД, zookeeper и kafka 3-мя последовательными командами:

Запустив скрипт (Для Windows необходимо использовать bash terminal: gitbash, cygwin или wsl)

```posh
bash localenv.sh
```

Или выполнив последовательно команды, для *nix:

```posh
docker run --name rococo-db -p 5432:5432 -e POSTGRES_PASSWORD=secret -e CREATE_DATABASES=rococo-artists,rococo-auth,rococo-countries,rococo-files,rococo-museums,rococo-paintings,rococo-users -e TZ=GMT+3 -e PGTZ=GMT+3 -v pgdata:/var/lib/postgresql/data -v ./postgres/script:/docker-entrypoint-initdb.d -d postgres:16.8 --max_connections=200 --max_prepared_transactions=100

docker run --name=zookeeper -e ZOOKEEPER_CLIENT_PORT=2181 -p 2181:2181 -d confluentinc/cp-zookeeper:7.3.2

docker run --name=kafka -e KAFKA_BROKER_ID=1 \
-e KAFKA_ZOOKEEPER_CONNECT=$(docker inspect zookeeper --format='{{ .NetworkSettings.IPAddress }}'):2181 \
-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
-e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
-e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
-e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
-p 9092:9092 -d confluentinc/cp-kafka:7.3.2
```

Для Windows (Необходимо использовать bash terminal: gitbash, cygwin или wsl):

```posh
docker run --name rococo-db -p 5432:5432 -e POSTGRES_PASSWORD=secret -e CREATE_DATABASES=rococo-artists,rococo-auth,rococo-countries,rococo-files,rococo-museums,rococo-paintings,rococo-users -e TZ=GMT+3 -e PGTZ=GMT+3 -v pgdata:/var/lib/postgresql/data -v ./postgres/script:/docker-entrypoint-initdb.d -d postgres:16.8 --max_connections=200 --max_prepared_transactions=100

docker run --name=zookeeper -e ZOOKEEPER_CLIENT_PORT=2181 -p 2181:2181 -d confluentinc/cp-zookeeper:7.3.2

docker run --name=kafka -e KAFKA_BROKER_ID=1 -e KAFKA_ZOOKEEPER_CONNECT=$(docker inspect zookeeper --format="{{ .NetworkSettings.IPAddress }}"):2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 -p 9092:9092 -d confluentinc/cp-kafka:7.3.2
```

[Про IP zookeeper](https://github.com/confluentinc/cp-docker-images/issues/801#issuecomment-692085103)

Если вы используете Windows и контейнер с БД не стартует с ошибкой в логе:

```
server started
/usr/local/bin/docker-entrypoint.sh: running /docker-entrypoint-initdb.d/init-database.sh
/usr/local/bin/docker-entrypoint.sh: /docker-entrypoint-initdb.d/init-database.sh: /bin/bash^M: bad interpreter: No such file or directory
```

То необходимо выполнить следующие команды в каталоге /postgres/script :

```
sed -i -e 's/\r$//' init-database.sh
chmod +x init-database.sh
```

#### 5. Установить Java версии 21. Это необходимо, т.к. проект использует синтаксис Java 21

Версию установленной Java необходимо проверить командой `java -version`

```posh
User-MacBook-Pro ~ % java -version
openjdk version "21.0.1" 2023-10-17 LTS
OpenJDK Runtime Environment Temurin-21.0.1+12 (build 21.0.1+12-LTS)
OpenJDK 64-Bit Server VM Temurin-21.0.1+12 (build 21.0.1+12-LTS, mixed mode)
```

Если у вас несколько версий Java одновременно - то хотя бы одна из них должна быть 21
Если java не установлена вовсе, то рекомендую установить OpenJDK (например,
из https://adoptium.net/en-GB/temurin/releases/)

#### 6. Установить пакетый менеджер для сборки front-end npm

[Инструкция](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm).
Рекомендованная версия Node.js - 22.6.0

# Запуск Rococo локально в IDE:

#### 0. Добавить необходимые переменные окружения.
Генерируем Fine-grained personal access token тут: https://github.com/settings/personal-access-tokens с
минимальными правами на чтение issue.
Добавляем переменные окружения: **GITHUB_TOKEN_NAME** (имя ключа) и **GITHUB_TOKEN** (сам ключ).

#### 1. Перейти в каталог

```posh
User-MacBook-Pro rococo % cd rococo-frontend
```

#### 2. Запустить фронтенд в режиме preview (сначала обновить зависимости)

```posh
User-MacBook-Pro rococo-frontend % npm i
User-MacBook-Pro rococo-frontend % npm run dev
```

#### 3. Прописать run конфигурацию для всех сервисов rococo-* - Active profiles local

Для этого зайти в меню Run -> Edit Configurations -> выбрать main класс -> указать Active profiles: local
[Инструкция](https://stackoverflow.com/questions/39738901/how-do-i-activate-a-spring-boot-profile-when-running-from-intellij).

#### 4 Запустить сервис Rococo-auth c помощью gradle или командой Run в IDE:

- Запустить сервис auth

```posh
User-MacBook-Pro rococo % cd rococo-auth
User-MacBook-Pro rococo-auth % gradle bootRun --args='--spring.profiles.active=local'
```

Или просто перейдя к main-классу приложения RococoAuthApplication выбрать run в IDEA (предварительно удостовериться что
выполнен предыдущий пункт)

Если сервис не стартует с ошибкой:

```posh
FATAL: database "rococo-auth" does not exist
```

то необходимо проверить, было ли сообщение об автоматическом создании баз данныхз в логе контейнера с Postgres (
rococo-db):

```posh
docker logs -f rococo-db
... 
Multiple database creation requested: rococo-artists,rococo-auth,rococo-countries,rococo-files,rococo-museums,rococo-paintings,rococo-users"
...
```

Если сообщения нет, то необходимо создать базы данных вручную (при этом, мы создаем только пустые БД, без таблиц):

- Установить одну из программ для визуальной работы с Postgres. Например, PgAdmin, DBeaver или Datagrip.
- Подключиться к БД postgres (host: localhost, port: 5432, user: postgres, pass: secret, database name: postgres) из
  PgAdmin и создать пустые БД микросервисов

```sql
   create
database "rococo-artists" with owner postgres;
   create
database "rococo-auth" with owner postgres;
   create
database "rococo-countries" with owner postgres;
   create
database "rococo-files" with owner postgres;
   create
database "rococo-museums" with owner postgres;
   create
database "rococo-paintings" with owner postgres;
   create
database "rococo-users" with owner postgres;
```

Если при запуске сервисов выходит ошибка:

```
Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.


Reason: Failed to determine a suitable driver class
```

Значит не был передан профиль, либо он не корректный

#### 5. Запустить в любой последовательности другие сервисы: rococo-artists,rococo-countries,rococo-files,rococo-gateway,rococo-museums,rococo-paintings,rococo-users

#### 6. Запуск тестов в IDE

При запуске тестов необходимо передавать параметр `-Dtest.env=local`

# Развёртывание Rococo в Docker

#### 0. Добавить необходимые переменные окружения.
Генерируем Fine-grained personal access token тут: https://github.com/settings/personal-access-tokens с
минимальными правами на чтение issue.
Добавляем переменные окружения: **GITHUB_TOKEN_NAME** (имя ключа) и **GITHUB_TOKEN** (сам ключ).

#### 1. Создать бесплатную учетную запись на https://hub.docker.com/ (если отсутствует)

#### 2. Создать в настройках своей учетной записи access_token

[Инструкция](https://docs.docker.com/docker-hub/access-tokens/).

#### 3. Выполнить docker login с созданным access_token (в инструкции это описано)

#### 4. Прописать в etc/hosts элиас для Docker-имени

```posh
User-MacBook-Pro rococo % vi /etc/hosts
```

```posh
##
# Host Database
#
# localhost is used to configure the loopback interface
# when the system is booting.  Do not change this entry.
#
127.0.0.1  localhost
#
127.0.0.1  frontend.rococo.dc
127.0.0.1  auth.rococo.dc
127.0.0.1  gateway.rococo.dc
127.0.0.1  allure
127.0.0.1  selenoid
```

#### 5. Перейти в корневой каталог проекта

```posh
User-MacBook-Pro rococo % cd rococo
```

#### 6. Запустить все сервисы

```posh
bash docker-compose-dev.sh
```

Текущая версия `docker-compose-dev.sh` **удалит все запущенные Docker контейнеры в системе**, поэтому если у вас есть
созданные контейнеры для других проектов - отредактируйте строку ```posh docker rm $(docker ps -a -q)```, чтобы включить
в grep только те контейнеры, что непосредственно относятся к rococo.

Фронтенд Rococo при запуске в докере будет работать для вас по адресу http://frontend.rococo.dc,

Если при выполнении скрипта `docker-compose-dev.sh` вы получили ошибку:

```
* What went wrong:
Execution failed for task ':rococo-auth:jibDockerBuild'.
> com.google.cloud.tools.jib.plugins.common.BuildStepsExecutionException: 
Build to Docker daemon failed, perhaps you should make sure your credentials for 'registry-1.docker.io...
```

То необходимо убедиться, что в `$USER/.docker/config.json` файле отсутствует запись `"credsStore": "desktop"`
При наличии такого ключа в json, его надо удалить.
Если файл пустой, то возможно не выполнен `docker login`. Если выполнялся, то надо создать файл руками по пути
`$USER/.docker/config.json`
с содержимым,

```
 {
        "auths": {
                "https://index.docker.io/v1/": {}
        },
        "currentContext": "desktop-linux"
}
```

Если вы не можете подключиться к БД в docker, указывая верные login и password, то возможно у вас поднята другая база на
том же порту 5432.
Это известная проблема, что Postgres в docker может стартануть при зянятом порту 5432, надо убедиться что у вас не
поднят никакой другой Postgres на этом порту.

Если вы используете Windows и контейнер с БД не стартует с ошибкой в логе:

```
server started
/usr/local/bin/docker-entrypoint.sh: running /docker-entrypoint-initdb.d/init-database.sh
/usr/local/bin/docker-entrypoint.sh: /docker-entrypoint-initdb.d/init-database.sh: /bin/bash^M: bad interpreter: No such file or directory
```

То необходимо выполнить следующие команды в каталоге env/docker/postgres :

```
sed -i -e 's/\r$//' init-database.sh
chmod +x init-database.sh
```

# Запуск тестов в Docker:

#### 1. Перейти в корневой каталог проекта

```posh
User-MacBook-Pro rococo % cd rococo
```

#### 2. Запустить все сервисы и тесты:

```posh
bash docker-compose-e2e.sh
```

Если нужно указать браузер, то отправляем параметр chrome или firefox.
При повторных запусках можно использовать флаг skip-build, чтобы повторно не собирать приложение.

```posh
bash docker-compose-e2e.sh chrome
```

```posh
bash docker-compose-e2e.sh firefox --skip-build
```

#### 3. Allure report будет доступен по адресу: http://localhost:5051/allure-docker-service/projects/rococo-ng/reports/latest/index.html

#### 4. Allure-UI: http://localhost:5051/

#### 5. Selenoid-UI: http://localhost:5052/

# Образец отчёта о тестировании

<img src="/images/allure-report.png" alt="allure-report" width="800"/>
<img src="/images/allure-report-behaviours.png" alt="allure-report behaviors" width="800"/>