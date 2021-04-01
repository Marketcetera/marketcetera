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
export COMMON_HOME=${METC_HOME}/common
export EXSIM_HOME=${METC_HOME}/exsim
export UI_HOME=${METC_HOME}/ui
export UBUNTU_MENUPROXY=0
export PATH=${DARE_HOME}/bin:${EXSIM_HOME}/bin:${PATH}
