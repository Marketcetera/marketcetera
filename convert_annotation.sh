#!/bin/bash

# Script to convert javax.annotation imports to jakarta.annotation
# Usage: ./convert_annotation.sh [directory_path]

set -e

# Default to current directory if no argument provided
TARGET_DIR=${1:-.}

echo "Converting javax.annotation to jakarta.annotation in $TARGET_DIR"

# Find all Java files with javax.annotation imports
FILES_TO_CONVERT=$(grep -l "import javax\.annotation" $(find "$TARGET_DIR" -name "*.java") 2>/dev/null || echo "")

if [ -z "$FILES_TO_CONVERT" ]; then
    echo "No files found with javax.annotation imports."
    exit 0
fi

echo "Found $(echo "$FILES_TO_CONVERT" | wc -l | tr -d '[:space:]') files to convert."

# Process each file
for FILE in $FILES_TO_CONVERT; do
    echo "Processing $FILE"
    
    # Replace imports using sed
    sed -i '' 's/import javax\.annotation\./import jakarta.annotation./g' "$FILE"
    
    echo "Converted $FILE"
done

echo "Conversion complete. Converted $(echo "$FILES_TO_CONVERT" | wc -l | tr -d '[:space:]') files."