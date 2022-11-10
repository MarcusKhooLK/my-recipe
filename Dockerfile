FROM maven:3-openjdk-18 AS builder

WORKDIR /app

COPY server/mvnw .
COPY server/mvnw.cmd .
COPY server/pom.xml .
COPY server/src src

# build
RUN mvn package -Dmaven.test.skip=true

FROM openjdk:18-oracle

WORKDIR /app

COPY --from=builder /app/target/server-0.0.1-SNAPSHOT.jar myrecipe.jar

# env variable
#ENV OPEN_WEATHER_MAP = "set this"
#ENV SERVER_PORT=4000

EXPOSE 8080

ENTRYPOINT java -jar myrecipe.jar