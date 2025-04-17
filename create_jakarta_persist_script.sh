#!/bin/bash
# Script to add Jakarta Persistence API dependency to all modules that need it

modules=(
  "admin/admin-server/pom.xml"
  "dare/pom.xml"
  "trade/trade-server/pom.xml"
  "fix/fix-server/pom.xml"
  "metrics/metrics-db/pom.xml"
  "modules/machine-learning/tensorflow/pom.xml"
  "strategy/strategy-server/pom.xml"
)

for module in "${modules[@]}"; do
  echo "Adding Jakarta Persistence API to $module"
  
  # Add the dependency if it doesn't exist already
  if ! grep -q "<artifactId>jakarta.persistence-api</artifactId>" "$module"; then
    # Insert dependency right before closing </dependencies> tag
    sed -i '' 's/<\/dependencies>/    <dependency>\n      <groupId>jakarta.persistence<\/groupId>\n      <artifactId>jakarta.persistence-api<\/artifactId>\n      <version>3.1.0<\/version>\n    <\/dependency>\n  <\/dependencies>/g' "$module"
  fi
done

echo "Jakarta Persistence API added to all necessary modules"