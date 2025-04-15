#!/bin/bash

# Script to update JWS imports from javax to jakarta
# Usage: ./update_jws_imports.sh <directory>

if [ -z "$1" ]; then
  echo "Usage: $0 <directory>"
  exit 1
fi

DIR_PATH="$1"

# Create a backup directory
BACKUP_DIR="${DIR_PATH}/jws_backup_$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"

echo "Backing up files to: $BACKUP_DIR"

# Find all Java files with javax.jws imports
FILES_TO_CONVERT=$(grep -l "import javax.jws" $(find "$DIR_PATH" -name "*.java") 2>/dev/null)

# Function to convert a file
convert_file() {
  local file="$1"
  echo "Converting: $file"
  
  # Create backup
  cp "$file" "$BACKUP_DIR/$(basename "$file")"
  
  # Replace imports
  sed -i '' 's/import javax.jws/import jakarta.jws/g' "$file"
}

echo "Found $(echo "$FILES_TO_CONVERT" | wc -l | tr -d ' ') files to convert"

# Process each file
for file in $FILES_TO_CONVERT; do
  convert_file "$file"
done

echo "Conversion complete. Backup files are in $BACKUP_DIR"