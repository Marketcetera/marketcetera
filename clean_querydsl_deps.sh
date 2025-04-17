#!/bin/bash
# Script to clean up QueryDSL dependencies in module pom.xml files

files=(
  "core/pom.xml" 
  "trade/trade-server/pom.xml" 
  "admin/admin-server/pom.xml" 
  "fix/fix-server/pom.xml" 
  "dare/pom.xml" 
  "modules/machine-learning/tensorflow/pom.xml" 
  "strategy/strategy-server/pom.xml"
)

for file in "${files[@]}"; do
  echo "Cleaning $file"
  
  # Fix querydsl-apt dependency format
  perl -i -pe 's/<artifactId>querydsl-apt<\/artifactId>\s*<classifier>jakarta<\/classifier>\s*<scope>provided<\/scope>/<artifactId>querydsl-apt<\/artifactId>\n      <classifier>jakarta<\/classifier>\n      <scope>provided<\/scope>/g' "$file"
  
  # Fix querydsl-jpa dependency format
  perl -i -pe 's/<artifactId>querydsl-jpa<\/artifactId>\s*<classifier>jakarta<\/classifier>\s*<\/dependency>/<artifactId>querydsl-jpa<\/artifactId>\n      <classifier>jakarta<\/classifier>\n    <\/dependency>/g' "$file"
done

echo "Finished cleaning QueryDSL dependencies."