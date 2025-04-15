#!/bin/bash

# Script to convert JUnit 4 to JUnit 5
# Usage: ./convert_junit.sh [directory_path]

set -e

# Default to current directory if no argument provided
TARGET_DIR=${1:-.}

echo "Converting JUnit 4 to JUnit 5 in $TARGET_DIR"

# Find all Java files with JUnit 4 imports
FILES_TO_CONVERT=$(grep -l "org\.junit\." $(find "$TARGET_DIR" -name "*.java") 2>/dev/null || echo "")

if [ -z "$FILES_TO_CONVERT" ]; then
    echo "No files found with JUnit 4 imports."
    exit 0
fi

echo "Found $(echo "$FILES_TO_CONVERT" | wc -l | tr -d '[:space:]') files to convert."

# Process each file
for FILE in $FILES_TO_CONVERT; do
    echo "Processing $FILE"
    
    # Replace imports
    sed -i '' 's/import org\.junit\.Test;/import org.junit.jupiter.api.Test;/g' "$FILE"
    sed -i '' 's/import org\.junit\.Before;/import org.junit.jupiter.api.BeforeEach;/g' "$FILE"
    sed -i '' 's/import org\.junit\.After;/import org.junit.jupiter.api.AfterEach;/g' "$FILE"
    sed -i '' 's/import org\.junit\.BeforeClass;/import org.junit.jupiter.api.BeforeAll;/g' "$FILE"
    sed -i '' 's/import org\.junit\.AfterClass;/import org.junit.jupiter.api.AfterAll;/g' "$FILE"
    sed -i '' 's/import org\.junit\.Ignore;/import org.junit.jupiter.api.Disabled;/g' "$FILE"
    
    # Replace static imports for assertions
    sed -i '' 's/import static org\.junit\.Assert\./import static org.junit.jupiter.api.Assertions./g' "$FILE"
    
    # Replace annotations
    sed -i '' 's/@Before/@BeforeEach/g' "$FILE"
    sed -i '' 's/@After/@AfterEach/g' "$FILE"
    sed -i '' 's/@BeforeClass/@BeforeAll/g' "$FILE"
    sed -i '' 's/@AfterClass/@AfterAll/g' "$FILE"
    sed -i '' 's/@Ignore/@Disabled/g' "$FILE"
    
    echo "Converted $FILE"
done

echo "Conversion complete. Converted $(echo "$FILES_TO_CONVERT" | wc -l | tr -d '[:space:]') files."
echo ""
echo "IMPORTANT: You may need to manually adjust assertion methods order:"
echo "- assertTrue(message, condition) -> assertTrue(condition, message)"
echo "- assertEquals(message, expected, actual) -> assertEquals(expected, actual, message)"
echo "- assertNotNull(message, object) -> assertNotNull(object, message)"
echo ""
echo "Also update your pom.xml with JUnit Jupiter dependencies:"
echo "<dependency>"
echo "  <groupId>org.junit.jupiter</groupId>"
echo "  <artifactId>junit-jupiter-api</artifactId>"
echo "  <version>5.10.2</version>"
echo "  <scope>test</scope>"
echo "</dependency>"
echo "<dependency>"
echo "  <groupId>org.junit.jupiter</groupId>"
echo "  <artifactId>junit-jupiter-engine</artifactId>"
echo "  <version>5.10.2</version>"
echo "  <scope>test</scope>"
echo "</dependency>"