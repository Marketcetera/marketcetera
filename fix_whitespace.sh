#!/bin/bash
# Script to fix whitespace issues in module pom.xml files

files=(
  "metrics/metrics-db/pom.xml" 
  "core/pom.xml" 
  "trade/trade-server/pom.xml" 
  "admin/admin-server/pom.xml" 
  "fix/fix-server/pom.xml" 
  "dare/pom.xml" 
  "modules/machine-learning/tensorflow/pom.xml" 
  "strategy/strategy-server/pom.xml"
)

for file in "${files[@]}"; do
  echo "Fixing whitespace in $file"
  
  # Fix consecutive blank lines
  perl -i -pe 's/\n\s*\n\s*\n/\n\n/g' "$file"
  
  # Fix trailing whitespace
  perl -i -pe 's/ +$//' "$file"
  
  # Fix whitespace around empty version tags
  perl -i -pe 's/<classifier>jakarta<\/classifier>\s+<scope>/<classifier>jakarta<\/classifier>\n      <scope>/g' "$file"
  
  # Fix whitespace around plugin closing tag
  perl -i -pe 's/<\/plugin>\s+<\/plugins>/<\/plugin>\n    <\/plugins>/g' "$file"
done

echo "Finished fixing whitespace issues."