#!/bin/bash

. "$(dirname $0)/../../setEnv.sh"

CURRENT_VERSION=${1}
BACKUP_DIR=${METC_HOME}/backup

usage()
{
    echo "Usage: install.sh <current version>"
}

pushd ${METC_HOME}

if [ "${CURRENT_VERSION}" != "" ]; then
    echo "Backing up version ${CURRENT_VERSION}"
else
    usage
    exit 1
fi

# validate current version
find . -name 'sixer-core-${CURRENT_VERSION}.jar' > /dev/null 2>&1 || (echo "Cannot verify ${CURRENT_VERSION}";exit 2)
echo "Confirmed current version ${CURRENT_VERSION}"

# backup existing
echo "Backing up ${CURRENT_VERSION}"
BACKUP_FILE="${BACKUP_DIR}/backup-${CURRENT_VERSION}.tar.bz2"
mkdir -p ${BACKUP_DIR}
tar jcvf ${BACKUP_FILE} dare/bin dare/conf dare/contrib dare/lib ui/bin ui/lib ui/conf

# remove current version libs
echo "Removing current version JARs"
find . -name "*-${CURRENT_VERSION}.jar" -exec rm -f {} \;
rm dare/bin/* dare/conf/* dare/lib/* ui/bin/* ui/lib/* ui/conf/*

# install packages
echo "Installing new packages"
cd ${METC_HOME}/..
tar jxvf /home/ubuntu/packages/sixer-server-package.tar.bz2
tar jxvf /home/ubuntu/packages/sixer-ui-package.tar.bz2

chmod +x ${METC_HOME}/dare/contrib/install.sh

echo "New version installed"
popd
