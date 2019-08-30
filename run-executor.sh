#!/bin/sh
echo git pull...

project_name=$1

git pull

echo package lite-flow-executor...

mvn -U -T 1C -Ptest clean package -Dmaven.test.skip=true

echo stop lite-flow-executor...

PID=$(ps -ef | grep lite-flow-executor-web | grep -v grep | awk '{ print $2 }')
if [ -z "$PID" ]
then
    echo lite-flow-executor is not started...
else
    kill -9 $PID
    echo killed $PID.
fi

echo start lite-flow-executor...

cd lite-flow-executor/lite-flow-executor-web/target

nohup java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8869,suspend=n -jar lite-flow-executor-web-1.0.0.jar > /dev/null 2>&1 &

echo lite-flow-executor started
