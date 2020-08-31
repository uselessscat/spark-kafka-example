# docker build -f nifi.dockerfile -t nifi:latest . && docker run -ti --name nifi -p 8080:8080 nifi:latest
FROM debian:10-slim as downloader

RUN apt update && \
    apt install -y curl && \
    curl -o /nifi-1.12.0-bin.tar.gz https://downloads.apache.org/nifi/1.12.0/nifi-1.12.0-bin.tar.gz && \
    mkdir /nifi-1.12.0 && \
    tar -xzvf /nifi-1.12.0-bin.tar.gz -C /nifi-1.12.0

# debian 9 usa openjdk-8
FROM debian:9

EXPOSE 8080

RUN apt update && apt install -y openjdk-8-jre
COPY --from=downloader /nifi-1.12.0 /usr

ENTRYPOINT ["/usr/nifi-1.12.0/bin/nifi.sh", "run"]