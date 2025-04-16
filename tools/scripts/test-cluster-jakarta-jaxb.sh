#!/bin/bash
# Script to test JAXB functionality in the cluster-core module with Jakarta EE

echo "Testing JAXB functionality in cluster-core with Jakarta EE properties"

# Set JAXB system properties for Jakarta EE compatibility
export MAVEN_OPTS="-Djakarta.xml.bind.JAXBContextFactory=org.eclipse.persistence.jaxb.JAXBContextFactory -Djavax.xml.accessExternalDTD=all -Djavax.xml.accessExternalSchema=all"

# Navigate to the cluster-core directory
cd ../../cluster/cluster-core

# Clean and compile the project
mvn clean compile

# Run tests with Jakarta EE system properties
mvn test

# Return status code
exit $?