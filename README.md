## Nifi

```shell
docker build -f nifi.dockerfile -t nifi:latest .
docker run -ti --name nifi -p 8080:8080 nifi:latest
```

## Nifi registry

```shell
docker build -f nifi_registry.dockerfile -t nifi_registry:latest .
docker run -ti --name nifi_registry -p 18080:18080 \
    -v /data/nifi_registry/flow_storage:/usr/nifi-registry-0.7.0/flow_storage \
    -v /data/nifi_registry/extension_bundles:/usr/nifi-registry-0.7.0/extension_bundles \
    nifi_registry:latest
```
