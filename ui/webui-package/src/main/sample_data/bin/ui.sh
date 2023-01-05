#!/bin/sh

. "$(dirname $0)/../../setEnv.sh"

app_stop()
{
    exit $1
}

trap app_stop EXIT INT TERM

APPLICATION_DIR=ui

cd ${UI_HOME}

THE_CLASSPATH=./conf
for file in `ls -1 ./lib/*.jar`
do
    THE_CLASSPATH=${THE_CLASSPATH}:${file}
done

rm -f ui.pid
#
# Set this to the log directory defined in the logger config file
LOGDIR=${UI_HOME}/logs
# Set this to the name of the stderr/stdout file you want to write for each instance
LOGNAME=uiout
#
java -Xms384m -Xmx4096m -Xloggc:${LOGDIR}/ui_gc.out -server -Dorg.marketcetera.appDir=${METC_HOME}/${APPLICATION_DIR}\
 -XX:+UseParallelGC -XX:+AggressiveOpts -XX:+UseFastAccessorMethods\
 -cp "${THE_CLASSPATH}"\
 -Dspring.config.location=conf/application.properties\
 org.marketcetera.web.WebuiApplication $* &
retval=$?
pid=$!
[ ${retval} -eq 0 ] && [ ${pid} -eq ${pid} ] && echo ${pid} > ui.pid
exit ${retval}
