#!/bin/bash

# This script resolves Maven properties from environment variables
# Usage: ./property-resolver.sh [optional maven command args]

# Function to check if an environment variable exists
check_env_var() {
  local var_name=$1
  if [ -z "${!var_name}" ]; then
    echo "WARNING: Environment variable $var_name is not set"
    return 1
  else
    echo "Environment variable $var_name is set"
    return 0
  fi
}

# Check required environment variables
echo "Checking required environment variables..."
check_env_var "METC_JDBC_PASSWORD"
check_env_var "METC_JDBC_DRIVER" 
check_env_var "METC_JDBC_URL"
check_env_var "METC_JDBC_TESTQUERY"
check_env_var "METC_HIBERNATE_DIALECT"
check_env_var "METC_FLYWAY_VENDOR"

# Set property arguments
MVN_PROPS="-Dmetc.jdbc.password=${METC_JDBC_PASSWORD} \
  -Dmetc.jdbc.driver=${METC_JDBC_DRIVER} \
  -Dmetc.jdbc.url=${METC_JDBC_URL} \
  -Dmetc.jdbc.testquery=${METC_JDBC_TESTQUERY} \
  -Dmetc.hibernate.dialect=${METC_HIBERNATE_DIALECT} \
  -Dmetc.flyway.vendor=${METC_FLYWAY_VENDOR}"

# Combine with any additional arguments
MVN_CMD="mvn ${MVN_PROPS} $@"

echo "Executing: ${MVN_CMD}"
eval ${MVN_CMD}