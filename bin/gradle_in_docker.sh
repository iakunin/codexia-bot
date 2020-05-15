#!/usr/bin/env bash

docker run \
    --tty \
    --interactive \
    --privileged \
    --rm \
    --volume="${PWD}":/home/gradle/project \
    --volume="${HOME}"/.m2:/root/.m2 \
    --volume=codexia-bot-gradle-cache:/home/gradle/.gradle \
    --volume=/var/run/docker.sock:/var/run/docker.sock \
    --workdir=/home/gradle/project \
    gradle:6.3-jdk11 \
    gradle "$@"

docker run \
    --tty \
    --interactive \
    --privileged \
    --rm \
    --volume="${PWD}":/home/gradle/project \
    --workdir=/home/gradle/project \
    gradle:6.3-jdk11 \
    chown -R "$(id -u)":"$(id -g)" .
