FROM debian:latest as downloader

RUN apt update && \
    apt install -y curl && \
    curl -o /nifi-1.12.0-bin.tar.gz https://downloads.apache.org/nifi/1.12.0/nifi-1.12.0-bin.tar.gz && \
    mkdir /nifi-1.12.0 && \
    tar -xzvf /nifi-1.12.0-bin.tar.gz -C /nifi-1.12.0

# nifi 1.12 uses java 11
FROM openjdk:11-jre-slim

COPY --from=downloader /nifi-1.12.0 /usr

EXPOSE 8080

ENTRYPOINT ["/usr/nifi-1.12.0/bin/nifi.sh", "run"]