#!/bin/bash

# This script can be used during CI troubleshooting to verify that
# environment variables are correctly set and accessed

echo "Verifying environment variables for CI build..."
echo "METC_JDBC_DRIVER: ${METC_JDBC_DRIVER}"
echo "METC_JDBC_URL: ${METC_JDBC_URL}"
echo "METC_JDBC_TESTQUERY: ${METC_JDBC_TESTQUERY}"
echo "METC_HIBERNATE_DIALECT: ${METC_HIBERNATE_DIALECT}"
echo "METC_FLYWAY_VENDOR: ${METC_FLYWAY_VENDOR}"
echo "METC_JDBC_PASSWORD: ****** (hidden for security)"

# Verify Java version
echo "Java version:"
java -version

# Verify Maven settings
echo "Maven settings:"
mvn -v

echo "Environment verification complete."