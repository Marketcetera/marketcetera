#!/bin/bash

. "$(dirname $0)/../../setEnv.sh"

service=/usr/sbin/service
tar=/bin/tar
rm=/bin/rm
backupdir=${METC_HOME}/backup

function finish {
    popd
    echo
    echo "===================================="
    echo "Rollback database now (if necessary)"
    echo "===================================="
    echo
    read -n 1 -s -r -p "Press any key to continue"
    echo
    echo "==========================="
    echo "Starting Marketcetera stack"
    echo "==========================="
    echo
    sudo service dare start
}

# stop system
echo
echo "==========================="
echo "Stopping Marketcetera stack"
echo "==========================="
echo

sudo service dare stop

pushd ${METC_HOME}

trap finish EXIT

# provide choice of rollback options

i=0
for version in `ls ${backupdir}| grep -Eo '[0-9]+\.[0-9]+\.[0-9]+'`
do
    options[$i]=${version}
    ((i++))
done
options[$i]="Quit"
echo
PS3='Please enter your choice for version to rollback to: '
select opt in "${options[@]}"
do
    if [ "${opt}" == "Quit" ]; then
	echo
	echo "************************"
	echo "Quitting with no changes"
	echo "************************"
	echo
	exit 2
    fi
    if [ "${opt}" == "" ]; then
	echo
	echo "**************"
	echo "Invalid option"
	echo "**************"
	echo
    else
	echo
	echo "======================"
	echo "Rolling back to ${opt}"
	echo "======================"
	echo
	break
    fi
done

cd ${METC_HOME}
backupfile="${backupdir}/backup-${opt}.tar.bz2"
if [ -f "${backupfile}" ]; then
    echo
    echo "======================"
    echo "Verified ${backupfile}"
    echo "======================"
    echo
else
    echo
    echo "***************************************************************"
    echo "Cannot roll back to ${opt} because ${backupfile} does not exist"
    echo "***************************************************************"
    echo
    exit 3
fi
echo
echo "=================================="
echo "Removing current version artifacts"
echo "=================================="
echo
${rm} -rf dare/bin dare/conf dare/contrib dare/lib ui/bin ui/lib ui/conf
echo
echo "====================================="
echo "Installing previous version artifacts"
echo "====================================="
echo
${tar} jxvf ${backupfile}
echo
echo "====================="
echo "Rolled back to ${opt}"
echo "====================="
echo
