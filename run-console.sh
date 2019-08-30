#!/bin/sh
echo git pull...

project_name=$1

git pull

echo package lite-flow-console...

mvn -U -T 1C -Ptest clean package -Dmaven.test.skip=true

echo stop lite-flow-console...

PID=$(ps -ef | grep lite-flow-console-web | grep -v grep | awk '{ print $2 }')
if [ -z "$PID" ]
then
    echo lite-flow-console is not started...
else
    kill -9 $PID
    echo killed $PID.
fi

echo start lite-flow-console...

cd lite-flow-console/lite-flow-console-web/target

nohup java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8868,suspend=n -jar lite-flow-console-web-1.0.0.jar > /dev/null 2>&1 &

echo lite-flow-console started
