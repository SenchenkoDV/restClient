FROM openjdk:8
ADD target/restClient-1.0-SNAPSHOT.jar restClient-1.0-SNAPSHOT.jar
EXPOSE 8082
CMD java -jar restClient-1.0-SNAPSHOT.jar