#!/bin/bash
# Script to update Hibernate dependencies for Spring Boot 3

echo "Updating Hibernate dependencies for Spring Boot 3..."

# Find pom files with Hibernate
pom_files=$(find . -name "pom.xml" -type f -exec grep -l "hibernate" {} \;)
changed_files=0

# Process pom files
for pom_file in $pom_files; do
  echo "Processing $pom_file"
  
  # Create a backup
  cp "$pom_file" "${pom_file}.bak"
  
  # Apply fixes
  perl -pi -e 's/<artifactId>hibernate-core<\/artifactId>(?![\s\S]*?<version>)/<artifactId>hibernate-core<\/artifactId>\n      <version>6.2.13.Final<\/version>/g' "$pom_file"
  perl -pi -e 's/<artifactId>hibernate-entitymanager<\/artifactId>/<artifactId>hibernate-core<\/artifactId>\n      <version>6.2.13.Final<\/version>/g' "$pom_file"
  perl -pi -e 's/<artifactId>hibernate-validator<\/artifactId>/<artifactId>jakarta.validation-api<\/artifactId>\n      <version>3.0.2<\/version>/g' "$pom_file"
  perl -pi -e 's/<artifactId>hibernate-jpamodelgen<\/artifactId>(?![\s\S]*?<version>)/<artifactId>hibernate-jpamodelgen<\/artifactId>\n      <version>6.2.13.Final<\/version>/g' "$pom_file"
  
  # Check if file was changed
  if cmp -s "$pom_file" "${pom_file}.bak"; then
    echo "  No changes made"
    rm "${pom_file}.bak"
  else
    echo "  Updated file"
    changed_files=$((changed_files + 1))
    rm "${pom_file}.bak"
  fi
done

echo "Hibernate dependency updates complete! Updated $changed_files files."