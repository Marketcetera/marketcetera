#!/bin/bash
# Script to fix QueryDSL configuration in module pom.xml files to use parent pom configuration

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
  echo "Fixing $file"
  
  # 1. Remove empty version tags in QueryDSL dependencies
  sed -i '' 's/<version><\/version>//g' "$file"
  
  # 2. Replace the custom maven-compiler-plugin configuration with parent reference
  sed -i '' '/<\!-- QueryDSL annotation processor is configured in maven-compiler-plugin -->/,/<\/plugin>/c\
      <!-- QueryDSL annotation processor is configured in parent pom -->\
      <plugin>\
        <artifactId>maven-compiler-plugin</artifactId>\
      </plugin>' "$file"
done

echo "Finished updating QueryDSL configuration in all files."