#!/bin/bash
#
# Script to test Jakarta EE compatibility in JAX-WS tests
#

set -e

# Directory of this script
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$SCRIPT_DIR/../.."

# Set up debug flags
export MAVEN_OPTS="-Dorg.apache.cxf.stax.allowInsecureParser=1"

# Method 1: Run specifically selected tests with Jakarta EE compatibility
if [ "$1" = "--selective" ] || [ "$1" = "" ]; then
  # Run the compatibility test first
  echo "Running compatibility test..."
  cd "$PROJECT_ROOT"
  mvn test -pl util -Dtest=org.marketcetera.util.ws.compatibility.JakartaCompatibilityTest -DfailIfNoTests=false

  # Run the basic stateless client-server tests (Jakarta EE compatible version)
  echo "Running basic stateless client-server tests (Jakarta EE compatible)..."
  mvn test -pl util -Dtest=org.marketcetera.util.ws.stateless.StatelessClientServerBasicTest -DfailIfNoTests=false
  
  # Run the basic stateful client-server tests (Jakarta EE compatible version)
  echo "Running basic stateful client-server tests (Jakarta EE compatible)..."
  mvn test -pl util -Dtest=org.marketcetera.util.ws.stateful.ClientServerBasicTest -DfailIfNoTests=false

  # Run specific compatible tests
  echo "Running selected WS tests..."
  mvn test -pl util -Dtest=org.marketcetera.util.ws.tags.*Test -DfailIfNoTests=false
fi

# Method 2: Run using the Jakarta-compatible profile
if [ "$1" = "--profile" ]; then
  echo "Running Jakarta-compatible tests using Maven profile..."
  cd "$PROJECT_ROOT"
  mvn test -pl util -P jakarta-compatible
fi

# Run all WS tests if requested (many will fail with Jakarta EE)
if [ "$1" = "--all" ]; then
  echo "Running all WS tests (expect failures)..."
  mvn test -pl util -Dtest=org.marketcetera.util.ws.**.*Test -DfailIfNoTests=false
fi

echo "Testing completed."