#!/bin/sh

##
## Set environment variables for the Marketcetera Platform
##
## Future: If messages are an output of this script, then those messages
##         will have to be localized using resource bundles, not hard-coded.
##
## Author: klim@marketcetera.com
## Since: 1.5.0
## Version: $Id: setEnv.sh 16121 2012-01-18 22:03:10Z colin $
##

# Make sure we're not the root user.
if [ "$(id -u)" = "0" ]; then
    echo "This script must not be run as root." 1>&2
    exit 1
fi

export METC_HOME="/opt/Marketcetera"
export DATA_HOME="/opt/Marketcetera"

export DARE_HOME=${METC_HOME}/dare
export EXSIM_HOME=${METC_HOME}/exsim
export PHOTON_HOME=${METC_HOME}/photon
export SE_HOME=${METC_HOME}/strategyengine
export UI_HOME=${METC_HOME}/ui
export UBUNTU_MENUPROXY=0
export PATH=${METC_HOME}/jdk1.8.0_261/bin:${DARE_HOME}/bin:${EXSIM_HOME}/bin:${PHOTON_HOME}/:${SE_HOME}/bin:${PATH}
export LD_LIBRARY_PATH=${SE_HOME}/modules/lib:${LD_LIBRARY_PATH}
