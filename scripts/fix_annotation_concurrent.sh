#!/bin/bash

# Script to update jakarta.annotation.concurrent imports to use the correct annotations
# Usage: ./fix_annotation_concurrent.sh <directory>

if [ -z "$1" ]; then
  echo "Usage: $0 <directory>"
  exit 1
fi

DIR_PATH="$1"

# Create a backup directory
BACKUP_DIR="${DIR_PATH}/concurrent_backup_$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"

echo "Backing up files to: $BACKUP_DIR"

# Find all Java files with jakarta.annotation.concurrent imports
FILES_TO_CONVERT=$(grep -l "import jakarta.annotation.concurrent" $(find "$DIR_PATH" -name "*.java") 2>/dev/null)

# Function to convert a file
convert_file() {
  local file="$1"
  echo "Converting: $file"
  
  # Create backup
  cp "$file" "$BACKUP_DIR/$(basename "$file")"
  
  # Replace imports and annotations
  sed -i '' 's/import jakarta.annotation.concurrent.Immutable;/import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;/g' "$file"
  sed -i '' 's/import jakarta.annotation.concurrent.ThreadSafe;/import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;/g' "$file"
  sed -i '' 's/import jakarta.annotation.concurrent.NotThreadSafe;/import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;/g' "$file"
  sed -i '' 's/import jakarta.annotation.concurrent.GuardedBy;/import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;/g' "$file"
  
  # Replace annotation usages
  sed -i '' 's/@Immutable/@SuppressFBWarnings(value="IMMUTABLE_CLASS")/g' "$file"
  sed -i '' 's/@ThreadSafe/@SuppressFBWarnings(value="THREAD_SAFETY")/g' "$file"
  sed -i '' 's/@NotThreadSafe/@SuppressFBWarnings(value="THREAD_SAFETY")/g' "$file"
  
  # Handle GuardedBy annotations
  # This is more complex and may need manual intervention
  sed -i '' 's/@GuardedBy(\(.*\))/@SuppressFBWarnings(value="GUARDED_BY_VIOLATION")/g' "$file"
}

echo "Found $(echo "$FILES_TO_CONVERT" | wc -l | tr -d ' ') files to convert"

# Process each file
for file in $FILES_TO_CONVERT; do
  convert_file "$file"
done

echo "Conversion complete. Backup files are in $BACKUP_DIR"
echo "Warning: You may need to manually review usages of @GuardedBy as they require more careful migration."