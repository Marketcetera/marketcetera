#!/bin/sh

##
## Stop server components for the Marketcetera Platform
##
## Future: If messages are an output of this script, then those messages
##         will have to be localized using resource bundles, not hard-coded.
##
## Author: klim@marketcetera.com
## Since: 1.5.0
## Version: $Id: stopServerComponents.sh 15403 2009-04-22 23:30:16Z klim $
##

. "$(dirname $0)/setEnv.sh"

cd ${DARE_HOME}
if [ -f dare.pid ]
then
    kill `cat dare.pid`
else
    pkill -f java
fi

#cd ${UI_HOME}
#if [ -f ui.pid ]
#then
#    kill `cat ui.pid`
#else
#    pkill -f java
#fi
