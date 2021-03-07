FROM hub.c.163.com/library/java:latest
VOLUME /tmp
EXPOSE 8081
ADD ./target/stf-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]