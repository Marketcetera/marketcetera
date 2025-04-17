#!/bin/bash
# Script to fix duplicate plugin tags in pom.xml files

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
  # Replace the duplicate plugin tags
  sed -i '' 's/<plugin>.*<!-- QueryDSL annotation processor is configured in maven-compiler-plugin -->.*<plugin>/<!-- QueryDSL annotation processor is configured in maven-compiler-plugin -->\n      <plugin>/g' "$file"
done

echo "Finished fixing duplicate plugin tags."