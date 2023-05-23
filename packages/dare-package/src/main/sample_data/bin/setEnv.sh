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

export METC_HOME="${installer:sys.installationDir}"
export DATA_HOME="${installer:sys.installationDir}"

export DARE_HOME=${METC_HOME}/dare
export PATH=${DARE_HOME}/bin:${PATH}
