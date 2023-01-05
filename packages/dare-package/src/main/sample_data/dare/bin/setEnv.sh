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

#
# These settings do not normally change
#
# set to validate or create. should normally be set to validate. if you set to create, this will drop all your existing tables and recreate them. if you set to create
#  you should set the DATABASE_MIGRATION to false.
export DATABASE_MODE=validate
# set to true or false. should normally be set to true. if you set to false, you should set DATABASE_MODE to create
export DATABASE_MIGRATION=true
#
# Change these settings as needed
#
# SENDER COMP ID for the Marketcetera exchange simulator - each installation needs a unique ID. It doesn't have to be your email address, just anything
#  that uniquely identifies this installation (we don't harvest or use your email address or anything else you use for your SENDER COMP ID).
export EXSIM_SENDER_COMPID=your-email-address-here
# database user id
export DATABASE_USER=metc
# database password
export DATABASE_PASSWORD=pw4metc
# database JDBC URL - this one is for postgres
export DATABASE_URL=jdbc:postgresql://localhost/metc
# database JDBC driver - this one is for postgres
export DATABASE_DRIVER=org.postgresql.Driver
# this is used for migration and is the value for postgres
export DATABASE_VENDOR=psql
