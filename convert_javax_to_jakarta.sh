#!/bin/bash
# Script to convert javax.* imports to jakarta.* imports for Spring Boot 3 and Jakarta EE compatibility

echo "Starting conversion of javax.* imports to jakarta.* imports..."

# Define replacements
replacements=(
  "s/import javax\\.annotation\\./import jakarta.annotation./g"
  "s/import javax\\.persistence\\./import jakarta.persistence./g"
  "s/import javax\\.xml\\.bind\\./import jakarta.xml.bind./g"
  "s/import javax\\.servlet\\./import jakarta.servlet./g"
  "s/import javax\\.transaction\\./import jakarta.transaction./g"
  "s/import javax\\.validation\\./import jakarta.validation./g"
  "s/import javax\\.inject\\./import jakarta.inject./g"
  "s/import javax\\.ws\\.rs\\./import jakarta.ws.rs./g"
  "s/import javax\\.jws\\./import jakarta.jws./g"
  "s/import javax\\.activation\\./import jakarta.activation./g"
  "s/import javax\\.xml\\.ws\\./import jakarta.xml.ws./g"
  "s/import javax\\.mail\\./import jakarta.mail./g"
)

# Initialize counter for changed files
changed_files=0

# Process each Java file
while IFS= read -r file; do
  if [ -f "$file" ]; then
    echo "Processing $file"
    
    # Create a backup
    cp "$file" "${file}.bak"
    
    # Apply all replacements
    for pattern in "${replacements[@]}"; do
      perl -pi -e "$pattern" "$file"
    done
    
    # Check if file was changed
    if cmp -s "$file" "${file}.bak"; then
      echo "  No changes made"
      rm "${file}.bak"
    else
      echo "  Updated file"
      changed_files=$((changed_files + 1))
      rm "${file}.bak"
    fi
  fi
done < <(find . -name "*.java" -type f)

echo "Conversion complete! Updated $changed_files files."