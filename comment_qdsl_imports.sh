#\!/bin/bash
# Script to comment out QueryDSL Q* class imports

DIR=${1:-.}

# Find all Java files with QueryDSL Q* imports
FILES=$(grep -r "import.*\.Q[A-Z][a-zA-Z0-9]*;" "$DIR" --include="*.java" | cut -d: -f1 | sort -u)

if [ -z "$FILES" ]; then
  echo "No files found with QueryDSL Q* imports"
  exit 0
fi

for FILE in $FILES; do
  echo "Commenting out QueryDSL imports in $FILE"
  # Comment out all import statements for Q* classes
  sed -i '' 's/\(import.*\.Q[A-Z][a-zA-Z0-9]*;\)/\/\/ Temporarily commented out for Spring Boot 3 migration\n\/\/ \1/g' "$FILE"
done

echo "Commented out QueryDSL Q* imports"
