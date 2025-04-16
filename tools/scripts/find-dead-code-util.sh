#!/bin/bash
# Script to specifically analyze the util module for dead code and problematic JAX-WS/Jetty components

MODULE_DIR="/Users/colin/marketcetera/workspaces/sixer-java11/code/marketcetera/util"
BASE_DIR=$(dirname $(dirname $MODULE_DIR))

echo "Analyzing util module WebService components..."

# 1. Find all JAX-WS related classes
echo -e "\n--- JAX-WS Related Classes ---"
grep -r --include="*.java" "jakarta.jws" $MODULE_DIR/src | grep -v "import" | cut -d':' -f1 | sort | uniq

# 2. Find all classes related to the failing tests
echo -e "\n--- Classes Related to Failing Tests ---"
for class in "ClientServerTest" "StatelessClientServerTest" "RemoteExceptionTest" "TypeTest"; do
    echo "* Dependencies for $class:"
    grep -r --include="*.java" "$class" $MODULE_DIR/src | grep -v "package" | cut -d':' -f1 | sort | uniq
done

# 3. Check for unused test classes (classes only referenced in test files but not main code)
echo -e "\n--- Test-Only Classes ---"
find $MODULE_DIR/src/test -name "*.java" -type f | while read -r file; do
    class_name=$(basename "$file" .java)
    main_usages=$(grep -r --include="*.java" --exclude="*Test.java" "$class_name" $MODULE_DIR/src/main | wc -l)
    if [ "$main_usages" -eq 0 ]; then
        echo "$class_name: No references in main code"
    fi
done

# 4. Identify potential candidates for removal
echo -e "\n--- Potential Removal Candidates ---"
echo "1. Web Service Tests (consistently failing):"
echo "   - ClientServerTest and related base classes"
echo "   - StatelessClientServerTest"
echo "   - TypeTest (related to WS types)"
echo "   - RemoteExceptionTest"
echo ""
echo "2. Supporting classes that might be unused:"
echo "   - Classes that only depend on the above tests"
echo "   - JAX-WS support classes that aren't used elsewhere"

# 5. Suggest a migration strategy
echo -e "\n--- Recommended Migration Approach ---"
echo "1. Create a new branch for this cleanup work"
echo "2. Comment out the failing tests"
echo "3. See if any dependent code is still needed"
echo "4. Remove unused JAX-WS/Jetty code gradually"
echo "5. Validate with a full build after each removal"
echo ""
echo "This is safer than removing all at once, and allows for testing the impact."