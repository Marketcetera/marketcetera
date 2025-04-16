#!/bin/bash

# Set system properties to ensure Jakarta EE's JAXB implementation is used
export MAVEN_OPTS="-Djakarta.xml.bind.JAXBContextFactory=org.eclipse.persistence.jaxb.JAXBContextFactory \
-Djakarta.xml.bind.context.factory=org.eclipse.persistence.jaxb.JAXBContextFactory \
-Djaxb.properties=/jakarta/xml/bind/jaxb.properties"

# Run the test
cd /Users/colin/marketcetera/workspaces/sixer-java11/code/marketcetera
mvn test -pl admin/admin-server -Dtest=org.marketcetera.admin.provisioning.ProvisioningAgentTest