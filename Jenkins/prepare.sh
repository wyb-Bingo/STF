#!/usr/bin/env bash

appName="stf:0.0.1-SNAPSHOT"
word = `docker ps -a | grep $appName  |  awk '{print $1}'`
echo "$word"

if [ -z "$word" ];
then
   echo "当前不存在该容器，直接进行启动该操作-------------------------------------"
else
  echo "删除容器--------------------------------------------"
  docker stop "$word"
  docker rm "$word"
  echo "删除镜像"
  docker rmi "$word"
fi
