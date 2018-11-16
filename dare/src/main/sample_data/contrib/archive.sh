#!/bin/bash

set -e

installdir=/opt/Marketcetera
archivedir="${installdir}/archive"
dumpfilename="${archivedir}/archive-`date +%Y%m%d-%H%M%S%Z`.sql"
archivesql="${installdir}/dare/contrib/archive.sql"
indexsql="${installdir}/dare/contrib/dare_index.sql"
hostname=localhost
user=metc
password=pw4metc
pgdump=/usr/bin/pg_dump
psql=/usr/bin/psql
gzip=/bin/gzip
runas=marketcetera
service=/usr/sbin/service

function clean_up {
  ${service} dare start
  exit
}

if [ -e /opt/Marketcetera/marketcetera/no_restart ];
then
  exit
fi

trap clean_up EXIT SIGHUP SIGINT SIGTERM

${service} dare stop

export PGPASSWORD="${password}"
su ${runas} -c "mkdir -p ${archivedir}"
su ${runas} -p -c "time ${pgdump} -C -O -h ${hostname} -U ${user} -f ${dumpfilename} metc"
su ${runas} -c "cd ${archivedir};${gzip} -9 ${dumpfilename} &"
su ${runas} -p -c "time ${psql} -h ${hostname} -U ${user} metc < ${archivesql}"
su ${runas} -p -c "time ${psql} -h ${hostname} -U ${user} metc < ${indexsql}"

clean_up
