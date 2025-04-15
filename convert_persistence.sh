#!/bin/bash
# Convert javax.persistence annotations to jakarta.persistence

# Find all Java files containing javax.persistence imports and replace them
find . -type f -name "*.java" | xargs grep -l "import javax.persistence" | xargs sed -i '' 's/import javax.persistence/import jakarta.persistence/g'

echo "Converted javax.persistence imports to jakarta.persistence"