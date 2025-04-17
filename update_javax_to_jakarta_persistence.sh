#!/bin/bash
# Script to update javax.persistence imports to jakarta.persistence

cd /Users/colin/marketcetera/workspaces/sixer-java11/code/marketcetera

# Find all files with javax.persistence imports and replace them with jakarta.persistence
find . -name "*.java" -exec grep -l "javax.persistence" {} \; | while read file; do
  echo "Updating $file"
  # Replace the imports
  sed -i '' 's/import javax.persistence/import jakarta.persistence/g' "$file"
  # Replace import statements that may be in Javadoc comments or other code
  sed -i '' 's/@javax.persistence/@jakarta.persistence/g' "$file"
done

echo "Javax persistence imports updated to Jakarta persistence"