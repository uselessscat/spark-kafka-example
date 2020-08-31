FROM debian:latest as downloader

RUN apt update && \
    apt install -y curl && \
    curl -o /nifi-registry-0.7.0-bin.tar.gz https://downloads.apache.org/nifi/nifi-registry/nifi-registry-0.7.0/nifi-registry-0.7.0-bin.tar.gz && \
    mkdir /nifi-registry-0.7.0 && \
    tar -xzvf /nifi-registry-0.7.0-bin.tar.gz -C /nifi-registry-0.7.0

FROM openjdk:11-jre-slim

COPY --from=downloader /nifi-registry-0.7.0 /usr
COPY ./config/nifi_registry/providers.xml /usr/nifi-registry-0.7.0/conf/providers.xml

EXPOSE 18080

ENTRYPOINT ["/usr/nifi-registry-0.7.0/bin/nifi-registry.sh", "run"]