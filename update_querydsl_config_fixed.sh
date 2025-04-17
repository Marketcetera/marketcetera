#!/bin/bash
# Script to replace apt-maven-plugin with maven-compiler-plugin configuration for QueryDSL

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

# Replace the apt-maven-plugin with maven-compiler-plugin configuration
for file in "${files[@]}"; do
  echo "Processing $file"
  
  # Create a temporary file
  temp_file=$(mktemp)
  
  # Read the file line by line
  while IFS= read -r line; do
    # If the line contains com.mysema.maven, replace it and the next line
    if [[ $line == *"com.mysema.maven"* ]]; then
      echo "      <!-- QueryDSL annotation processor is configured in maven-compiler-plugin -->" >> "$temp_file"
      echo "      <plugin>" >> "$temp_file"
      echo "        <groupId>org.apache.maven.plugins</groupId>" >> "$temp_file"
      echo "        <artifactId>maven-compiler-plugin</artifactId>" >> "$temp_file"
      echo "        <configuration>" >> "$temp_file"
      echo "          <annotationProcessorPaths>" >> "$temp_file"
      echo "            <path>" >> "$temp_file"
      echo "              <groupId>com.querydsl</groupId>" >> "$temp_file"
      echo "              <artifactId>querydsl-apt</artifactId>" >> "$temp_file"
      echo "              <version>\${querydsl.version}</version>" >> "$temp_file"
      echo "              <classifier>jakarta</classifier>" >> "$temp_file"
      echo "            </path>" >> "$temp_file"
      echo "            <path>" >> "$temp_file"
      echo "              <groupId>jakarta.persistence</groupId>" >> "$temp_file"
      echo "              <artifactId>jakarta.persistence-api</artifactId>" >> "$temp_file"
      echo "              <version>3.1.0</version>" >> "$temp_file"
      echo "            </path>" >> "$temp_file"
      echo "          </annotationProcessorPaths>" >> "$temp_file"
      echo "          <annotationProcessors>" >> "$temp_file"
      echo "            <annotationProcessor>com.querydsl.apt.jpa.JPAAnnotationProcessor</annotationProcessor>" >> "$temp_file"
      echo "          </annotationProcessors>" >> "$temp_file"
      echo "          <compilerArgs>" >> "$temp_file"
      echo "            <arg>-Aquerydsl.entityAccessors=true</arg>" >> "$temp_file"
      echo "            <arg>-Aquerydsl.packageSuffix=</arg>" >> "$temp_file"
      echo "          </compilerArgs>" >> "$temp_file"
      echo "        </configuration>" >> "$temp_file"
      echo "      </plugin>" >> "$temp_file"
      
      # Skip the next line (apt-maven-plugin line)
      read -r _ 
    else
      # Otherwise, just copy the line
      echo "$line" >> "$temp_file"
    fi
  done < "$file"
  
  # Replace the original file with the modified content
  mv "$temp_file" "$file"
done

echo "Finished updating QueryDSL configuration in all files."