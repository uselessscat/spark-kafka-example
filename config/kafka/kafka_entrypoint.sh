#!/bin/bash

# This is a test
$KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties &
zoekeeper=$!
exec $KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties &
kafka=$!

# /usr/kafka_2.13-2.6.0/bin/kafka-topics.sh --create --topic data --bootstrap-server localhost:9092

wait $kafka
kill -s TERM $zoekeeper
wait $zoekeeper
