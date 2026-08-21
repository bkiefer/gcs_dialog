if test -z "$NETWORK_NAME"; then
    NETWORK_NAME=docker0
fi
. utils.sh

docker run -i -t \
       --add-host=host.docker.internal:host-gateway \
       -v `pwd`/docker_config.yml:/config.yml \
       -v `pwd`/logs:/logs \
       --entrypoint /bin/sh \
       --name gcs_dialog_manager drz/gcs_dialog_manager:$(pom_version)
