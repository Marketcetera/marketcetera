#!/bin/bash
# Helper script to build the project with Java 17

# Set Java 17 home
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH

# Print Java version for verification
echo "Building with Java version:"
java -version
echo ""

# Run Maven with all arguments passed to this script
mvn "$@"