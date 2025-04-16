#!/bin/bash

# Script to run the ProvisioningAgentTest with Jakarta EE settings
# Fully migrated to Jakarta EE with no compatibility layer

# Set Maven options for Jakarta EE
export MAVEN_OPTS="-Djakarta.xml.bind.JAXBContextFactory=org.eclipse.persistence.jaxb.JAXBContextFactory \
-Dspring.classloader.jakarta-compatible=true \
-Djakarta.xml.accessExternalDTD=all"

# Set Java module path if using Java 9+
if [[ $(java -version 2>&1) =~ "version \"9" ]] || [[ $(java -version 2>&1) =~ "version \"1[0-9]" ]]; then
    echo "Running with Java 9+ - adding module flags"
    export MAVEN_OPTS="$MAVEN_OPTS --add-modules jakarta.xml.bind"
fi

# Set logging to debug level
export LOG_LEVEL=debug

echo "MAVEN_OPTS: $MAVEN_OPTS"

# Run the specific test class
echo "Running the ProvisioningAgentTest..."
cd /Users/colin/marketcetera/workspaces/sixer-java11/code/marketcetera
mvn test -pl admin/admin-server -Dtest=org.marketcetera.admin.provisioning.ProvisioningAgentTest