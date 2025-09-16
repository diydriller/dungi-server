#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/xircle
LOG_DIR=$REPOSITORY/logs
mkdir -p $LOG_DIR

echo "> 현재 구동 중인 애플리케이션 종료"

SERVICES=("api-server" "batch-server" "notification-server")

for SERVICE in "${SERVICES[@]}"
do
  echo "> $SERVICE 종료 시도"
  CURRENT_PID=$(pgrep -f "$SERVICE" | awk '{print $1}')

  if [ -z "$CURRENT_PID" ]; then
    echo ">> $SERVICE 구동 중인 프로세스 없음"
  else
    echo ">> kill -15 $CURRENT_PID"
    kill -15 $CURRENT_PID
    sleep 5
  fi
done

echo "> 새 애플리케이션 배포"

for SERVICE in "${SERVICES[@]}"
do
  JAR_NAME=$(ls -tr $REPOSITORY/$SERVICE*-SNAPSHOT.jar | tail -n 1)

  if [ -f "$JAR_NAME" ]; then
    echo ">> $SERVICE 실행: $JAR_NAME"
    chmod +x $JAR_NAME
    nohup java -jar -Duser.timezone=Asia/Seoul $JAR_NAME \
      >> $LOG_DIR/$SERVICE.log 2>&1 &
  else
    echo ">> $SERVICE JAR 파일 없음, 실행 건너뜀"
  fi
done