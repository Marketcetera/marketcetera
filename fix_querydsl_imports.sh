#!/bin/bash

# This script fixes the javax imports in QueryDSL generated classes
# and updates them to jakarta packages

set -e

# Find all QueryDSL generated files
echo "Finding all QueryDSL generated files..."
Q_FILES=$(find . -name "Q*.java")

# Show how many files we found
echo "Found $(echo "$Q_FILES" | wc -l) QueryDSL generated files"

# Perform search and replace for javax imports
for file in $Q_FILES; do
  echo "Processing $file..."
  # Replace javax.annotation.processing with jakarta.annotation.processing
  sed -i.bak 's/import javax\.annotation\.processing\./import jakarta.annotation.processing./g' "$file"
  # Replace javax.persistence with jakarta.persistence
  sed -i.bak 's/import javax\.persistence\./import jakarta.persistence./g' "$file"
  # Remove backup files
  rm -f "${file}.bak"
done

echo "All QueryDSL files processed"

# Clean and rebuild admin modules in order
echo "Rebuilding admin modules..."
mvn clean install -pl admin/admin-api,admin/admin-core,admin/admin-server -am

echo "Migration completed!"