#!/bin/bash

# Spring Boot 3.x to Jakarta EE Migration Helper Script
# This script helps with migrating from javax.* to jakarta.* packages
# for JPA/Persistence and fixing QueryDSL generated classes

set -e  # Exit on error

echo "Starting Jakarta EE migration for persistence and QueryDSL classes..."

# Step 1: Update Java files with javax.persistence imports
echo "Converting javax.persistence imports to jakarta.persistence..."
find . -name "*.java" -type f -not -path "*/target/*" -not -path "*/\.*" | xargs perl -pi -e 's/import javax\.persistence\./import jakarta.persistence./g'

# Step 2: Update Java files with javax.annotation.concurrent imports
echo "Converting javax.annotation.concurrent imports to net.jcip.annotations..."
find . -name "*.java" -type f -not -path "*/target/*" -not -path "*/\.*" | xargs perl -pi -e 's/import javax\.annotation\.concurrent\.NotThreadSafe/import net.jcip.annotations.NotThreadSafe/g'
find . -name "*.java" -type f -not -path "*/target/*" -not -path "*/\.*" | xargs perl -pi -e 's/import javax\.annotation\.concurrent\.ThreadSafe/import net.jcip.annotations.ThreadSafe/g'

# Step 3: Update other javax.annotation imports
echo "Converting other javax.annotation imports to jakarta.annotation..."
find . -name "*.java" -type f -not -path "*/target/*" -not -path "*/\.*" | xargs perl -pi -e 's/import javax\.annotation\./import jakarta.annotation./g'

# Step 4: Clean the project to remove generated files
echo "Cleaning project to remove generated files..."
mvn clean

# Step 5: Explicitly add Jakarta dependencies to modules that need them
echo "Ensuring Jakarta dependencies are present in all modules using JPA..."
modules_with_jpa=$(find . -name "*.java" -type f -not -path "*/target/*" -not -path "*/\.*" -exec grep -l "jakarta.persistence" {} \; | xargs dirname | sort -u | xargs dirname | sort -u)

for module_dir in $modules_with_jpa; do
  if [[ -f "$module_dir/pom.xml" ]]; then
    echo "Adding Jakarta Persistence API dependency to $module_dir"
    # Check if dependency already exists
    if ! grep -q "jakarta.persistence-api" "$module_dir/pom.xml"; then
      # Use sed to add dependency before </dependencies>
      sed -i.bak '/<\/dependencies>/i \
    <dependency>\
      <groupId>jakarta.persistence</groupId>\
      <artifactId>jakarta.persistence-api</artifactId>\
      <version>3.1.0</version>\
    </dependency>' "$module_dir/pom.xml"
      rm -f "${module_dir}/pom.xml.bak"
    fi
  fi
done

# Step 6: Rebuild core module first
echo "Building core module..."
mvn install -pl core -am

# Step 7: Rebuild admin modules in order
echo "Building admin modules..."
mvn install -pl admin/admin-api,admin/admin-core,admin/admin-server -am

# Step 8: Fix any QueryDSL generated files that still have javax imports
echo "Fixing imports in QueryDSL generated files..."
Q_FILES=$(find . -name "Q*.java" -path "*/target/generated-sources/*")

for file in $Q_FILES; do
  echo "Processing $file..."
  # Replace javax.annotation.processing with jakarta.annotation.processing
  perl -pi -e 's/import javax\.annotation\.processing\./import jakarta.annotation.processing./g' "$file"
  # Replace javax.persistence with jakarta.persistence
  perl -pi -e 's/import javax\.persistence\./import jakarta.persistence./g' "$file"
done

# Step 9: Check for any remaining javax.persistence imports to alert the user
REMAINING_JAVAX=$(find . -name "*.java" -type f -not -path "*/\.*" -exec grep -l "javax.persistence" {} \;)
if [[ -n "$REMAINING_JAVAX" ]]; then
  echo "WARNING: The following files still contain javax.persistence imports:"
  echo "$REMAINING_JAVAX"
  echo "You may need to manually update these files."
fi

echo "Migration completed! Remember to test the application thoroughly."