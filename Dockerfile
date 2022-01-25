FROM openjdk:8
MAINTAINER haridas <haridas.kakunje@tarento.com>
RUN mkdir -p /home/attachment
ADD target/form-service-0.0.1-SNAPSHOT.jar form-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/form-service-0.0.1-SNAPSHOT.jar"]
EXPOSE 8090
