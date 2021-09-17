ARG NIFI_VERSION=1.12.0

FROM debian:latest as downloader 

ARG NIFI_VERSION
ENV URL https://downloads.apache.org/nifi/${NIFI_VERSION}/nifi-${NIFI_VERSION}-bin.tar.gz
ENV URL_TOOLKIT https://downloads.apache.org/nifi/${NIFI_VERSION}/nifi-toolkit-${NIFI_VERSION}-bin.tar.gz

RUN apt update && \
    apt install -y curl && \
    curl -o /tmp/nifi-${NIFI_VERSION}-bin.tar.gz ${URL} && \
    curl -o /tmp/nifi-toolkit-${NIFI_VERSION}-bin.tar.gz ${URL_TOOLKIT} && \
    mkdir /nifi-${NIFI_VERSION} && \
    mkdir /nifi-toolkit-${NIFI_VERSION} && \
    tar -xzvf /tmp/nifi-${NIFI_VERSION}-bin.tar.gz -C /nifi-${NIFI_VERSION} && \
    tar -xzvf /tmp/nifi-toolkit-${NIFI_VERSION}-bin.tar.gz -C /nifi-toolkit-${NIFI_VERSION}

# nifi 1.12 uses java 11
FROM openjdk:11-jre-slim

ARG NIFI_VERSION

COPY --from=downloader /nifi-${NIFI_VERSION} /usr
COPY --from=downloader /nifi-toolkit-${NIFI_VERSION} /usr

EXPOSE 8080

ENTRYPOINT ["/usr/nifi-1.12.0/bin/nifi.sh", "run"]