#!/bin/bash
# Script to fix JPA entity annotations that might need special attention after Jakarta EE migration

echo "Starting JPA annotation fixes..."

# Find entity files
entity_files=$(find . -name "*.java" -type f -exec grep -l "@Entity" {} \;)
changed_files=0

# Process entity files
for file in $entity_files; do
  echo "Processing entity file: $file"
  
  # Create a backup
  cp "$file" "${file}.bak"
  
  # Apply fixes
  perl -pi -e 's/@Column\((.*?)length\s*=\s*([0-9]*)\)/@Column($1length = $2)/g' "$file"
  perl -pi -e 's/@JoinColumn\((.*?)nullable\s*=\s*(true|false)\)/@JoinColumn($1nullable = $2)/g' "$file"
  perl -pi -e 's/@GeneratedValue\(strategy\s*=\s*GenerationType\.AUTO\)/@GeneratedValue(strategy = GenerationType.IDENTITY)/g' "$file"
  
  # Check if file was changed
  if cmp -s "$file" "${file}.bak"; then
    echo "  No changes made"
    rm "${file}.bak"
  else
    echo "  Updated file"
    changed_files=$((changed_files + 1))
    rm "${file}.bak"
  fi
done

# Find files with Hibernate imports
hibernate_files=$(find . -name "*.java" -type f -exec grep -l "org.hibernate" {} \;)

# Process Hibernate files
for file in $hibernate_files; do
  echo "Checking Hibernate imports in: $file"
  
  # Create a backup
  cp "$file" "${file}.bak"
  
  # Apply fixes
  perl -pi -e 's/import org\.hibernate\.validator/import jakarta.validation/g' "$file"
  perl -pi -e 's/import org\.hibernate\.annotations\.CacheConcurrencyStrategy/import jakarta.persistence.CacheConcurrencyStrategy/g' "$file"
  
  # Check if file was changed
  if cmp -s "$file" "${file}.bak"; then
    echo "  No changes made"
    rm "${file}.bak"
  else
    echo "  Updated file"
    changed_files=$((changed_files + 1))
    rm "${file}.bak"
  fi
done

echo "JPA annotation fixes complete! Updated $changed_files files."