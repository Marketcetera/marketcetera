#\!/bin/bash
# Script to convert javax.persistence.FetchType to jakarta.persistence.FetchType

DIR=${1:-.}

# Find all Java files with javax.persistence.FetchType
FILES=$(grep -r "javax.persistence.FetchType" "$DIR" --include="*.java" | cut -d: -f1 | sort -u)

if [ -z "$FILES" ]; then
  echo "No files found with javax.persistence.FetchType"
  exit 0
fi

for FILE in $FILES; do
  echo "Converting $FILE"
  # Replace all javax.persistence.FetchType with jakarta.persistence.FetchType
  sed -i '' 's/javax\.persistence\.FetchType/jakarta.persistence.FetchType/g' "$FILE"
done

echo "Converted javax.persistence.FetchType to jakarta.persistence.FetchType"
