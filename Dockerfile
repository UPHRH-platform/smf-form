FROM openjdk:8
MAINTAINER haridas <haridas.kakunje@tarento.com>

ADD form-service-0.0.1-SNAPSHOT.jar form-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/form-service-0.0.1-SNAPSHOT.jar"]
EXPOSE 8099
