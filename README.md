# Run
## Docker compose

```Shell
docker-compose up 
```

## Manual
### Nifi

```Shell
docker build -f nifi.dockerfile -t nifi:latest .
docker run -ti --name nifi -p 8080:8080 nifi:latest
```

### Nifi registry

```Shell
docker build -f nifi_registry.dockerfile -t nifi_registry:latest .
docker run -ti --name nifi_registry -p 18080:18080 \
    -v "$(pwd)"/data/nifi_registry/flow_storage:/usr/nifi-registry-0.7.0/flow_storage \
    -v "$(pwd)"/data/nifi_registry/extension_bundles:/usr/nifi-registry-0.7.0/extension_bundles \
    nifi_registry:latest
```

### Kafka

```Shell
docker build -f kafka.dockerfile -t kafka:latest .
docker run -ti --name kafka kafka:latest
```

### Spark

```Shell
docker build -f spark.dockerfile -t spark:latest .
docker run -ti -v "$(pwd)"/src/scala:/usr/src/scala --name spark spark:latest
```