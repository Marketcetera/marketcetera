#\!/bin/bash
# Script to convert javax.persistence.* annotations to jakarta.persistence.*

DIR=${1:-.}

# Find all Java files with javax.persistence.* annotations
FILES=$(grep -r "@javax.persistence." "$DIR" --include="*.java" | cut -d: -f1 | sort -u)

if [ -z "$FILES" ]; then
  echo "No files found with javax.persistence.* annotations"
  exit 0
fi

for FILE in $FILES; do
  echo "Converting $FILE"
  # Replace all @javax.persistence.* with @jakarta.persistence.*
  sed -i '' 's/@javax\.persistence\./@jakarta.persistence./g' "$FILE"
done

echo "Converted qualified javax.persistence annotations to jakarta.persistence"
