FROM eclipse-temurin:11-jdk-alpine

VOLUME /tmp

COPY build/libs/*SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]