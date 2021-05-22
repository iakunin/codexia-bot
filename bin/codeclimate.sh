#!/usr/bin/env bash

docker run \
    --interactive --tty --rm \
    --env CODECLIMATE_CODE="${PWD}" \
    --volume "${PWD}":/code \
    --volume /var/run/docker.sock:/var/run/docker.sock \
    --volume /tmp/cc:/tmp/cc \
    codeclimate/codeclimate prepare

docker run \
    --interactive --tty --rm \
    --env CODECLIMATE_CODE="${PWD}" \
    --volume "${PWD}":/code \
    --volume /var/run/docker.sock:/var/run/docker.sock \
    --volume /tmp/cc:/tmp/cc \
    codeclimate/codeclimate analyze

docker run \
    --tty \
    --interactive \
    --privileged \
    --rm \
    --volume="${PWD}":/home/gradle/project \
    --workdir=/home/gradle/project \
    gradle:6.8-jdk11 \
    chown -R "$(id -u)":"$(id -g)" .
