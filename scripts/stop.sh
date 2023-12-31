#!/usr/bin/env bash

ABSPATH=$(readlink -f $0) #stop.sh가 속해있는 경로 확인
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh # profile.sh의 여러가지 function을 사용할 수 있도록 하는 명령어

IDLE_PORT=$(find_idle_port)

echo "> $IDLE_PORT 에서 구동 중인 애플리케이션 pid 확인"
IDLE_PID=$(lsof -ti tcp:${IDLE_PORT})

if [ -z ${IDLE_PID} ]
then
  echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $IDLE_PID"
  kill -15 ${IDLE_PID}
  sleep 5
fi
