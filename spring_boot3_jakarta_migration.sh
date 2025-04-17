#!/bin/bash

# Master script for Spring Boot 3.x and Jakarta EE migration
# This script orchestrates the entire migration process from Spring Boot 2.7 to 3.2.5

set -e  # Exit on error

echo "===================================================="
echo "Spring Boot 3.x and Jakarta EE Migration"
echo "===================================================="

# Make all scripts executable
chmod +x fix_querydsl_imports.sh
chmod +x migration_jakarta_persistence.sh
chmod +x hibernate_spring_boot3_fixes.sh

# Step 1: Migrate Jakarta Persistence and fix QueryDSL
echo "Step 1: Migrating to Jakarta Persistence API and fixing QueryDSL..."
./migration_jakarta_persistence.sh

# Step 2: Fix Hibernate 6 and connection pool issues
echo "Step 2: Fixing Hibernate 6 and HikariCP configuration..."
./hibernate_spring_boot3_fixes.sh

# Step 3: Fix any remaining QueryDSL issues
echo "Step 3: Fixing any remaining QueryDSL issues..."
./fix_querydsl_imports.sh

# Step 4: Build the project
echo "Step 4: Building the entire project..."
mvn clean install -DskipTests

# Step 5: Run tests
echo "Step 5: Running tests..."
mvn test

echo "===================================================="
echo "Migration completed!"
echo "===================================================="
echo "IMPORTANT: Please review the logs for any errors and "
echo "           manually fix any remaining issues."
echo "===================================================="