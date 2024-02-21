if test -z "$NETWORK_NAME"; then
    NETWORK_NAME=docker0
fi
#DOCKER_HOST=$(ip -4 addr show $NETWORK_NAME| grep -Po 'inet \K[\d.]+')
# if the broker is provided by an OS service, make sure that the mosquitto
# config contains the following two lines
#
# listener 1883 0.0.0.0
# allow_anonymous true
DOCKER_HOST=host.docker.internal
sed 's/\( *brokerhost:\).*/\1 '"$DOCKER_HOST/" config.yml > docker_config.yml
docker run -d --rm \
       --add-host=host.docker.internal:host-gateway \
       -v `pwd`/docker_config.yml:/config.yml \
       -v `pwd`/logs:/logs \
       --name gcs_dialog_manager drz/gcs_dialog_manager:latest
