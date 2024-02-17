if test -z "$NETWORK_NAME"; then
    NETWORK_NAME=docker0
fi
DOCKER_HOST=$(ip -4 addr show $NETWORK_NAME| grep -Po 'inet \K[\d.]+')
sed 's/\( *brokerhost:\).*/\1 '"$DOCKER_HOST/" config.yml > docker_config.yml
docker run --restart always -d \
       -v `pwd`/docker_config.yml:/config.yml \
       -v `pwd`/logs:/logs \
       --name gcs_dialog_manager drz/gcs_dialog_manager:latest
