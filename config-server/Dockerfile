FROM openjdk:8-jdk-alpine

ADD config config-server/config
ADD build/libs/config-server-0.1.0.jar app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]