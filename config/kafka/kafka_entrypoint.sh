#!/bin/bash

# This is a test
${KAFKA_HOME}/bin/zookeeper-server-start.sh ${KAFKA_HOME}/config/zookeeper.properties &
zoekeeper=$!
exec ${KAFKA_HOME}/bin/kafka-server-start.sh ${KAFKA_HOME}/config/server.properties &
kafka=$!

# ${KAFKA_HOME}/bin/kafka-topics.sh --create --topic data --bootstrap-server localhost:9092
# ${KAFKA_HOME}/bin/kafka-console-producer.sh --topic data --bootstrap-server localhost:9092

wait $kafka
kill -s TERM $zoekeeper
wait $zoekeeper
