#!/bin/bash

# Script to convert javax.* imports to jakarta.* in Java files
# Usage: ./convert_javax_to_jakarta.sh <directory>

if [ -z "$1" ]; then
  echo "Usage: $0 <directory>"
  exit 1
fi

DIR_PATH="$1"

# Create a backup directory
BACKUP_DIR="${DIR_PATH}/javax_backup_$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"

echo "Backing up files to: $BACKUP_DIR"

# Find all Java files with javax imports
FILES_TO_CONVERT=$(grep -l "import javax\." $(find "$DIR_PATH" -name "*.java") 2>/dev/null)

# Function to convert a file
convert_file() {
  local file="$1"
  echo "Converting: $file"
  
  # Create backup
  cp "$file" "$BACKUP_DIR/$(basename "$file")"
  
  # Replace imports
  sed -i '' 's/import javax\.annotation\./import jakarta.annotation./g' "$file"
  sed -i '' 's/import javax\.inject\./import jakarta.inject./g' "$file"
  sed -i '' 's/import javax\.jms\./import jakarta.jms./g' "$file"
  sed -i '' 's/import javax\.persistence\./import jakarta.persistence./g' "$file"
  sed -i '' 's/import javax\.transaction\./import jakarta.transaction./g' "$file"
  sed -i '' 's/import javax\.ws\.rs\./import jakarta.ws.rs./g' "$file"
  sed -i '' 's/import javax\.servlet\./import jakarta.servlet./g' "$file"
  sed -i '' 's/import javax\.xml\./import jakarta.xml./g' "$file"
  sed -i '' 's/import javax\.validation\./import jakarta.validation./g' "$file"
  sed -i '' 's/import javax\.ejb\./import jakarta.ejb./g' "$file"
  sed -i '' 's/import javax\.mail\./import jakarta.mail./g' "$file"
  sed -i '' 's/import javax\.json\./import jakarta.json./g' "$file"
  sed -i '' 's/import javax\.enterprise\./import jakarta.enterprise./g' "$file"
  sed -i '' 's/import javax\.el\./import jakarta.el./g' "$file"
  sed -i '' 's/import javax\.interceptor\./import jakarta.interceptor./g' "$file"
  sed -i '' 's/import javax\.resource\./import jakarta.resource./g' "$file"
  sed -i '' 's/import javax\.faces\./import jakarta.faces./g' "$file"
  sed -i '' 's/import javax\.batch\./import jakarta.batch./g' "$file"
  sed -i '' 's/import javax\.websocket\./import jakarta.websocket./g' "$file"
}

echo "Found $(echo "$FILES_TO_CONVERT" | wc -l | tr -d ' ') files to convert"

# Process each file
for file in $FILES_TO_CONVERT; do
  convert_file "$file"
done

echo "Conversion complete. Backup files are in $BACKUP_DIR"
echo "You may need to manually update additional references to javax in code (not just imports)."