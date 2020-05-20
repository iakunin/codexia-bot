#!/usr/bin/env bash

DIR=$(cd -P -- "$(dirname -- "$0")" && pwd -P)
cd "$DIR"/.. || exit


printf "Building image:\n"
IMAGE=$(docker build -q -f Dockerfile-liquibase .)

printf "\n\nNewly built image:\n"
echo "$IMAGE"

printf "\n\nRunning liquibase:\n"
docker run \
    --tty \
    --interactive \
    --rm \
    --net host \
    --env LIQUIBASE_HOST="$REMOTE_DB_HOST" \
    --env LIQUIBASE_PORT=5432 \
    --env LIQUIBASE_DATABASE=codexia-bot \
    --env LIQUIBASE_USERNAME=codexia-bot \
    --env LIQUIBASE_PASSWORD="$REMOTE_DB_PASSWORD" \
    "$IMAGE" \
    liquibase update

printf "\n\nDeleting image:\n"
docker image rm "$IMAGE"
