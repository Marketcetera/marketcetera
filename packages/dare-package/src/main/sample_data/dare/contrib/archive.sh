#!/bin/bash

set -e

installdir=/opt/Marketcetera
dare_contrib_dir=${installdir}/dare/contrib
archivedir="${installdir}/archive"
timestamp="`date +%Y%m%d-%H%M%S%Z`"
dumpfilename="${archivedir}/archive-${timestamp}.sql"
archivesql="${dare_contrib_dir}/archive.sql"
errorfilename="/tmp/archive-${timestamp}_error.txt"
statusfilename="/tmp/archive-${timestamp}_status.txt"
hostname=35.194.183.59
user=postgres
psqldump=/usr/bin/pg_dump
psql=/usr/bin/psql
gzip=/bin/gzip
service=/usr/sbin/service
err_mailing_list=colin@marketcetera.com
status_mailing_list=colin@marketcetera.com
crontab=/usr/bin/crontab
sudo=/usr/bin/sudo
signal_file=/tmp/exchange-shutdown

function err_report() {
    echo "Error on line $1" >> ${errorfilename}
    dos2unix -o ${errorfilename} > /dev/null 2>&1
    mail -s "Daily Start/Stop/Archive Error" ${err_mailing_list} < ${errorfilename}
    rm -f ${errorfilename}
}

function clean_up {
    ${sudo} ${service} dare start >> ${statusfilename} 2>> ${errorfilename}
    echo "DARE restarted at `date`" >> ${statusfilename} 2>> ${errorfilename}
    dos2unix -o ${statusfilename} > /dev/null 2>&1
    mail -s "Daily Start/Stop/Archive `hostname` Status" ${status_mailing_list} < ${statusfilename}
    rm -f ${statusfilename}
    exit
}

function wait_to_restart {
    while [ ! -f "${signal_file}" ]; do
      seconds=`echo $(($(date -f - +%s- <<< $'today 20:10\nnow')0))`
      if [ "$seconds" -lt "1" ]; then
          echo "Must restart now" >> ${statusfilename} 2>> ${errorfilename}
          break
      fi
      sleep 1
    done
    echo "Beginning DARE archive at `date`" >> ${statusfilename} 2>> ${errorfilename}
}

if [ -e ${installdir}/no_restart ];
then
    exit
fi

trap 'err_report $LINENO' ERR
trap clean_up EXIT SIGHUP SIGINT SIGTERM

touch ${statusfilename}
touch ${errorfilename}

wait_to_restart

${sudo} ${service} dare stop >> ${statusfilename} 2>> ${errorfilename}
echo "DARE stopped at `date`" >> ${statusfilename} 2>> ${errorfilename}

cd ${dare_contrib_dir}

mkdir -p ${archivedir} >> ${statusfilename} 2>> ${errorfilename}
time ${psql} "sslmode=verify-ca sslrootcert=server-ca.pem sslcert=client-cert.pem sslkey=client-key.pem hostaddr=${hostname} user=${user} dbname=postgres" < ${archivesql}
echo "Archive complete at `date`" >> ${statusfilename} 2>> ${errorfilename}
