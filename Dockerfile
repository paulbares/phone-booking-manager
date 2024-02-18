FROM maven:3.9.6-eclipse-temurin-17

RUN apt-get update
RUN apt-get install -y git
RUN git clone https://github.com/paulbares/digital-wallet.git

CMD ["./digital-wallet/mvnw"]
