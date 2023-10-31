#!/usr/bin/env bash

ABSPATH=$(readlink -f $0) #start.sh가 속해있는 경로 확인
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh # profile.sh의 여러가지 function을 사용할 수 있도록 하는 명령어

function switch_proxy() {
  IDLE_PORT=$(find_idle_port)

  echo "> 전환할 Port: $IDLE_PORT"
  echo "> Port 전환"
  echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" | sudo tee /etc/nginx/conf.d/service-url.inc

  echo "> Nginx Reload"
  sudo service nginx reload
}
