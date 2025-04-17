#!/bin/bash
# Master script to migrate to Spring Boot 3 and Jakarta EE

echo "Starting Spring Boot 3 migration process..."

# Step 1: Convert javax.* imports to jakarta.* imports
echo "Step 1: Converting javax.* imports to jakarta.* imports"
./convert_javax_to_jakarta.sh

# Step 2: Fix JPA annotations
echo "Step 2: Fixing JPA annotations"
./fix_jpa_annotations.sh

# Step 3: Update Hibernate dependencies
echo "Step 3: Updating Hibernate dependencies"
./update_hibernate_dependencies.sh

# Step 4: Clean and build to verify changes
echo "Step 4: Clean and build to verify changes"
mvn clean

echo "Migration process completed."
echo ""
echo "Next steps:"
echo "1. Run 'mvn install' to verify the build"
echo "2. Fix any compilation errors manually"
echo "3. Run tests to verify functionality"
echo ""