#!/bin/sh

. "$(dirname $0)/../../setEnv.sh"

app_stop()
{
    exit $1
}

trap app_stop EXIT INT TERM

APPLICATION_DIR=dare

cd ${METC_HOME}/${APPLICATION_DIR}

THE_CLASSPATH=./conf
for file in `ls -1 ./lib/*.jar`
do
    THE_CLASSPATH=${THE_CLASSPATH}:${file}
done

rm -f dare.pid
#
# Set this to the log directory defined in log4j2.xml
LOGDIR=logs
# Set this to the name of the stderr/stdout file you want to write for each instance
LOGNAME=dareout
# Set this to a relative directory to the cluster installation
INSTANCE_DIR=instances
#
# These port values are used for day-to-day work, just make sure they are unique and leave enough room for each instance
#
# DARE RPC port
RPC_PORT=9100
# Cluster port
CLUSTER_PORT=9400
# STOMP port
STOMP_PORT=9500
# JMS port
JMS_PORT=9600
# DARE JMS JMX port
JMS_JMX_PORT=9900
#
# These values are typically the ones you'll need to change
#
# This port value is what web services connect to
# DARE WS port
WS_PORT=9000
#
# This port value is what incoming FIX sessions would connect to
ACCEPTOR_PORT=9800
#
# Set this to a comma-separated list of cluster hosts
CLUSTER_TCPIP_MEMBERS=127.0.0.1
#
# Set this to the total number of instances you want to create, excluding the master instance
TOTAL_INSTANCES=1
#
# Set this to the number of milliseconds to delay between starting instances
INSTANCE_START_DELAY=1000
#
# Set this to the min heap size for each instance
INSTANCE_XMS=1024m
# Set this to the max heap size for each instance
INSTANCE_XMX=8192m
#
java -Xms384m -Xmx1024m -Xloggc:${LOGDIR}/dare_gc.out -server -Dorg.marketcetera.appDir=${METC_HOME}/${APPLICATION_DIR}\
 -XX:+UseParallelGC -XX:+AggressiveOpts -XX:+UseFastAccessorMethods\
 -Dlog4j.configurationFile=${METC_HOME}/${APPLICATION_DIR}/conf/log4j2.xml\
 -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector\
 -cp "${THE_CLASSPATH}"\
 -Dmetc.cluster.tcpip.members=${CLUSTER_TCPIP_MEMBERS}\
 -Dmetc.logdir=${LOGDIR}\
 -Dmetc.logname=${LOGNAME}\
 -Dmetc.total.instances=${TOTAL_INSTANCES}\
 -Dmetc.instance.Xms${INSTANCE_XMS}\
 -Dmetc.instance.Xmx${INSTANCE_XMX}\
 -Dmetc.start.delay=${INSTANCE_START_DELAY}\
 -Dmetc.port.metc.rpc.port=${RPC_PORT}\
 -Dmetc.port.metc.ws.port=${WS_PORT}\
 -Dmetc.port.metc.cluster.port=${CLUSTER_PORT}\
 -Dmetc.port.metc.stomp.port=${STOMP_PORT}\
 -Dmetc.port.metc.jms.port=${JMS_PORT}\
 -Dmetc.port.metc.jms.jmx.port=${JMS_JMX_PORT}\
 -Dmetc.port.metc.acceptor.qf.port=${ACCEPTOR_PORT}\
 -Dorg.marketcetera.instanceDir=${INSTANCE_DIR}\
 -Dmetc.instance.XX:+UseParallelGC\
 -Dmetc.instance.XX:+AggressiveOpts\
 -Dmetc.instance.XX:+UseFastAccessorMethods\
 -Dmetc.instance.Xloggc:${LOGDIR}/dare_gc.out\
 -Dmetc.instance.Log4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector\
 -Dmetc.instance=0\
 org.marketcetera.core.MultiInstanceApplicationContainer $* &
retval=$?
pid=$!
[ ${retval} -eq 0 ] && [ ${pid} -eq ${pid} ] && echo ${pid} > dare.pid
exit ${retval}
