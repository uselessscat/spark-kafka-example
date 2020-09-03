ARG SPARK_VERSION=3.0.0
ARG PROJECT_NAME=spark_example_kinesis

FROM debian:latest as downloader

ARG SPARK_VERSION
ENV URL https://downloads.apache.org/spark/spark-${SPARK_VERSION}/spark-${SPARK_VERSION}-bin-hadoop3.2.tgz

RUN apt update && \
    apt install -y curl && \
    mkdir /spark-${SPARK_VERSION} && \
    echo "Downloading ... ${URL}" && \
    curl -o /spark-${SPARK_VERSION}-bin-hadoop3.2.tgz ${URL} && \
    tar -xzvf /spark-${SPARK_VERSION}-bin-hadoop3.2.tgz -C /spark-${SPARK_VERSION}

FROM openjdk:11-jre as builder

ARG SPARK_VERSION
ARG PROJECT_NAME
ENV PROJECT_DIR /usr/code/scala/${PROJECT_NAME}

COPY --from=downloader /spark-${SPARK_VERSION}/* /usr/spark-${SPARK_VERSION}
COPY ./code/scala/${PROJECT_NAME} ${PROJECT_DIR}

RUN echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add && \
    apt update && \
    apt install -y sbt

RUN cd ${PROJECT_DIR} && \
    sbt compile && \
    sbt package && \
    mkdir -p /usr/code/compiled/ && \
    mv ${PROJECT_DIR}/target/scala-2.12/${PROJECT_NAME}_2.12-1.0.jar /usr/code/compiled/${PROJECT_NAME}_app.jar

ENTRYPOINT [ "bash" ]

FROM openjdk:11-jre-slim

ARG SPARK_VERSION
ARG PROJECT_NAME

COPY --from=downloader /spark-${SPARK_VERSION}/* /usr/spark-${SPARK_VERSION}
COPY --from=builder /usr/code/compiled/${PROJECT_NAME}_app.jar /usr/projects/${PROJECT_NAME}_app.jar

ENTRYPOINT ["bash"]