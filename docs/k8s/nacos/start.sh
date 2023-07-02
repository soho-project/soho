#!/bin/bash

mkdir -p /tmp/nacos-tmp/
cd /tmp/nacos-tmp
git clone https://github.com/nacos-group/nacos-k8s.git

cd nacos-k8s
chmod +x quick-startup.sh
./quick-startup.sh

kubectl port-forward "service/nacos-headless" 8848
