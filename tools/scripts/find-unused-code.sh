#!/bin/bash
# Script to identify potentially unused classes and methods in the codebase

# Check if a module path is provided
if [ $# -eq 0 ]; then
  echo "Usage: $0 <module-path>"
  echo "Example: $0 util"
  exit 1
fi

MODULE=$1
BASE_DIR=$(pwd)
MODULE_DIR="$BASE_DIR/$MODULE"

if [ ! -d "$MODULE_DIR" ]; then
  echo "Module directory not found: $MODULE_DIR"
  exit 1
fi

echo "Analyzing module: $MODULE"

# Step 1: Generate JaCoCo coverage report
echo "Generating code coverage report..."
mvn clean test jacoco:report -pl $MODULE

# Step 2: Find Java files with no test coverage
echo -e "\n--- Java files with potentially low/no test coverage ---"
find "$MODULE_DIR/target/site/jacoco" -name "*.java.html" -type f | while read -r file; do
  coverage=$(grep -o '<td class="ctr2">.*%</td>' "$file" | head -1 | sed 's/<td class="ctr2">\(.*\)<\/td>/\1/')
  if [[ "$coverage" == "0%" || "$coverage" == "n/a" ]]; then
    class_name=$(basename "$file" .java.html)
    echo "$class_name: $coverage coverage"
  fi
done

# Step 3: Find classes that are only used in test code
echo -e "\n--- Classes only referenced in test code ---"
find "$MODULE_DIR/src/main/java" -name "*.java" -type f | while read -r file; do
  class_name=$(basename "$file" .java)
  main_usages=$(grep -r --include="*.java" --exclude-dir="*test*" "$class_name" "$BASE_DIR" | wc -l)
  test_usages=$(grep -r --include="*.java" --include-dir="*test*" "$class_name" "$BASE_DIR" | wc -l)
  
  if [ "$main_usages" -eq 0 ] && [ "$test_usages" -gt 0 ]; then
    echo "$class_name: Only used in tests"
  fi
done

# Step 4: Find methods with @Deprecated annotation (candidates for removal)
echo -e "\n--- Deprecated methods (candidates for removal) ---"
grep -r --include="*.java" "@Deprecated" "$MODULE_DIR/src" | grep -v "test" | sed 's/.*\/\([^\/]*\.java\):\(.*\)/@Deprecated in \1: \2/'

# Step 5: Find outdated dependencies that might indicate dead code
echo -e "\n--- Checking for outdated dependencies ---"
grep -r "javax\." --include="*.java" "$MODULE_DIR/src/main" | grep -v "import javax.annotation.concurrent" | head -10

echo -e "\nAnalysis complete. Review results above for code cleanup candidates."