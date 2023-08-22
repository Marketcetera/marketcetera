#!/bin/bash

## 
## Start server components for the Marketcetera Platform
## 
## Future: If messages are an output of this script, then those messages
##         will have to be localized using resource bundles, not hard-coded.
## 
## Author: klim@marketcetera.com
## Since: 1.5.0
## Version: $Id: startServerComponents.sh 15467 2009-06-10 17:06:18Z klim $
##

. "$(dirname $0)/setEnv.sh"

cd ${DARE_HOME}
./bin/dare.sh
