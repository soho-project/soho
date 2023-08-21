#!/bin/bash

script_dir=$(dirname "$0")
cd $script_dir
cd ../../

DEMO_DOCKER='docker -H ssh://root@43.153.34.211'
SERVICE_NAME='soho-admin'
SERVICE_PORT="6677"

mvn package -DskipTests
$DEMO_DOCKER stop "$SERVICE_NAME"
$DEMO_DOCKER rm "$SERVICE_NAME"
$DEMO_DOCKER build --build-arg CONFIG_PROFILE=demo -t "$SERVICE_NAME" .
$DEMO_DOCKER run -d --name "$SERVICE_NAME" -p "$SERVICE_PORT:$SERVICE_PORT" "$SERVICE_NAME"
