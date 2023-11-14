#!/usr/bin/env bash

ABSPATH=$(readlink -f $0) #start.sh가 속해있는 경로 확인
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh # profile.sh의 여러가지 function을 사용할 수 있도록 하는 명령어

BASE_PATH=/home/ec2-user/app
DEPLOY_PATH=$BASE_PATH/zip
BUILD_PATH=$(ls $DEPLOY_PATH/*.jar)
REPOSITORY=$BASE_PATH/jar

echo "> Build 파일 복사"
echo "> cp $BUILD_PATH $REPOSITORY/"
cp $BUILD_PATH $REPOSITORY/

echo "> 새 애플리케이션 배포"
JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> JAR Name : $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

IDLE_PROFILE=$(find_idle_profile)

echo "> $JAR_NAME 를 profile=$IDLE_PROFILE 로 실행합니다."
nohup java -jar \
      -Dspring.profiles.active=$IDLE_PROFILE \
      -Dspring.config.location=classpath:application.yaml,/home/ec2-user/app/application-credential.yaml,/home/ec2-user/app/application-oauth.yaml \
      $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &