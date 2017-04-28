#!/bin/sh

##
## Set environment variables for the Marketcetera Platform
##
## Future: If messages are an output of this script, then those messages
##         will have to be localized using resource bundles, not hard-coded.
##
## Author: klim@marketcetera.com
## Since: 1.5.0
## Version: $Id: setEnv.sh 84077 2014-09-22 21:57:31Z colin $
##

METC_HOME="/opt/Marketcetera"
ORDERLOADER_HOME=${METC_HOME}/orderloader
DARE_HOME=${METC_HOME}/dare
EXSIM_HOME=${METC_HOME}/exsim
SE_HOME=${METC_HOME}/strategyengine
UBUNTU_MENUPROXY=0
PATH=${ORDERLOADER_HOME}/bin:${DARE_HOME}/bin:${EXSIM_HOME}/bin:${SE_HOME}/bin:${PATH}
LD_LIBRARY_PATH=${SE_HOME}/modules/lib:${LD_LIBRARY_PATH}
export METC_HOME ORDERLOADER_HOME DARE_HOME SE_HOME PATH LD_LIBRARY_PATH UBUNTU_MENUPROXY EXSIM_HOME
