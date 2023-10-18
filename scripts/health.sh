#!/usr/bin/env bash

ABSPATH=$(readlink -f $0) #start.sh가 속해있는 경로 확인
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh # profile.sh의 여러가지 function을 사용할 수 있도록 하는 명령어
source ${ABSDIR}/switch.sh # switch.sh의 여러가지 function을 사용할 수 있도록 하는 명령어

IDLE_PORT=$(find_idle_port)

echo "> Health Check Start!"
echo "> IDLE_PORT: $IDLE_PORT"
echo "> curl -s http://localhost:$IDLE_PORT/profile "
sleep 10

for RETRY_COUNT in {1..10}
do
  RESPONSE=$(curl -s http://localhost:${IDLE_PORT}/profile)
  UP_COUNT=$(echo ${RESPONSE} | grep 'prod' | wc -l)

  if [ ${UP_COUNT} -ge 1 ]
  then # $up_count >= 1 ("prod" 문자열이 있는지 검증)
    echo "> Health check 성공"
    switch_proxy
    break
  else
    echo "> Health check의 응답을 알 수 없거나 혹은 실행 상태가 아닙니다."
    echo "> Health check: ${RESPONSE}"
  fi

  if [ ${RETRY_COUNT} -eq 10 ]
  then
    echo "> Health check 실패."
    echo "> 엔진엑스에 연결하지 않고 배포를 종료합니다."
    exit 1
  fi

  echo "> Health check 연결 실패. 재시도..."
  sleep 10
done
