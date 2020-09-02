FROM debian:latest as downloader

RUN apt update && \
    apt install -y curl && \
    curl -o /kafka_2.13-2.6.0.tgz https://downloads.apache.org/kafka/2.6.0/kafka_2.13-2.6.0.tgz && \
    mkdir /kafka_2.6.0 && \
    tar -xzvf /kafka_2.13-2.6.0.tgz -C /kafka_2.6.0

FROM openjdk:11-jre-slim

ENV KAFKA_HOME /usr/kafka_2.6.0
COPY --from=downloader /kafka_2.6.0/* /usr/kafka_2.6.0

COPY ./config/kafka/kafka_entrypoint.sh /opt/scripts/kafka_entrypoint.sh

EXPOSE 9092

ENTRYPOINT [ "/opt/scripts/kafka_entrypoint.sh" ]

