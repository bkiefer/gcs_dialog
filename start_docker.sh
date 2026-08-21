. $(dirname $0)/utils.sh

docker run -i -t --rm \
       --add-host=host.docker.internal:host-gateway \
       -v `pwd`/docker_config.yml:/app/config.yml \
       -v `pwd`/logs:/app/logs \
       --name gcs_dialog_manager drz/gcs_dialog_manager:$(pom_version)
