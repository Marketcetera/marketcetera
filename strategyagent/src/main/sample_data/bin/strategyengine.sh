#!/bin/sh

. "$(dirname $0)/../../setEnv.sh"

APPLICATION_DIR=strategyengine

cd ${METC_HOME}/${APPLICATION_DIR}

rm -f strategyengine.pid

THE_CLASSPATH=./conf
for file in `ls -1 ./lib/*.jar`
do
    THE_CLASSPATH=${THE_CLASSPATH}:${file}
done

java -Xms384m -Xmx2048m -XX:MaxPermSize=512m -server -Dorg.marketcetera.appDir=${METC_HOME}/${APPLICATION_DIR}\
 -XX:+UseParallelGC -XX:+AggressiveOpts -XX:+UseFastAccessorMethods\
 -Dlog4j.configurationFile=${METC_HOME}/${APPLICATION_DIR}/conf/log4j2.xml\
 -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector\
 -Dstrategy.classpath=${METC_HOME}/${APPLICATION_DIR}/src\
 -Djava.library.path=${METC_HOME}/${APPLICATION_DIR}/modules/lib\
 -cp "${THE_CLASSPATH}"\
 org.marketcetera.core.ApplicationContainer samples/commands/sampleCommands.txt &
retval=$?
pid=$!
[ ${retval} -eq 0 ] && [ ${pid} -eq ${pid} ] && echo ${pid} > strategyengine.pid
exit ${retval}
 