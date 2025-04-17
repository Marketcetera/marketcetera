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
  
  # Add the querydsl-apt dependency with jakarta classifier if not already present
  if ! grep -q "<artifactId>querydsl-apt</artifactId>" "$file"; then
    # Find dependencies section and add our dependencies
    perl -i -pe 's/<\/dependencies>/  <!-- QueryDSL dependencies for Jakarta EE -->
    <dependency>
      <groupId>com.querydsl<\/groupId>
      <artifactId>querydsl-apt<\/artifactId>
      <classifier>jakarta<\/classifier>
      <version>${querydsl.version}<\/version>
      <scope>provided<\/scope>
    <\/dependency>
    <dependency>
      <groupId>com.querydsl<\/groupId>
      <artifactId>querydsl-jpa<\/artifactId>
      <classifier>jakarta<\/classifier>
      <version>${querydsl.version}<\/version>
    <\/dependency>
  <\/dependencies>/g' "$file"
  fi
  
  # Replace the apt-maven-plugin with maven-compiler-plugin configuration
  perl -i -pe 's/<plugin>\s*<groupId>com.mysema.maven<\/groupId>\s*<artifactId>apt-maven-plugin<\/artifactId>\s*<\/plugin>/<!-- QueryDSL annotation processor is configured in maven-compiler-plugin -->
      <plugin>
        <groupId>org.apache.maven.plugins<\/groupId>
        <artifactId>maven-compiler-plugin<\/artifactId>
        <configuration>
          <annotationProcessorPaths>
            <path>
              <groupId>com.querydsl<\/groupId>
              <artifactId>querydsl-apt<\/artifactId>
              <version>${querydsl.version}<\/version>
              <classifier>jakarta<\/classifier>
            <\/path>
            <path>
              <groupId>jakarta.persistence<\/groupId>
              <artifactId>jakarta.persistence-api<\/artifactId>
              <version>3.1.0<\/version>
            <\/path>
          <\/annotationProcessorPaths>
          <annotationProcessors>
            <annotationProcessor>com.querydsl.apt.jpa.JPAAnnotationProcessor<\/annotationProcessor>
          <\/annotationProcessors>
          <compilerArgs>
            <arg>-Aquerydsl.entityAccessors=true<\/arg>
            <arg>-Aquerydsl.packageSuffix=<\/arg>
          <\/compilerArgs>
        <\/configuration>
      <\/plugin>/g' "$file"
done

echo "Finished updating QueryDSL configuration in all files."