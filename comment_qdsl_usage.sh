#\!/bin/bash
# Script to comment out QueryDSL Q* class usage

DIR=${1:-.}

# Find all Java files with QueryDSL Q* class usage
FILES=$(grep -r "QPersistent" "$DIR" --include="*.java" | cut -d: -f1 | sort -u)

if [ -z "$FILES" ]; then
  echo "No files found with QueryDSL Q* class usage"
  exit 0
fi

for FILE in $FILES; do
  echo "Commenting out QueryDSL usage in $FILE"
  # Replace all Q* class references with a temporary comment
  sed -i '' 's/QPersistent/\/\* Temporarily commented out for Spring Boot 3 migration \*\/ QPersistent/g' "$FILE"
done

echo "Commented out QueryDSL Q* class usage"
