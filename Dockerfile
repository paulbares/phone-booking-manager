FROM maven:3.9.6-eclipse-temurin-17

RUN apt-get update
RUN apt-get install -y git
RUN git clone https://github.com/paulbares/phone-booking-manager.git

WORKDIR "/phone-booking-manager"
RUN mvn clean install -DskipTests
ENTRYPOINT ["java","-jar", "target/app-0.0.1-SNAPSHOT.jar"]