#!/bin/bash

# Script to fix Hibernate 6 and connection pool issues in Spring Boot 3.x

set -e

echo "Starting Hibernate and connection pool fixes for Spring Boot 3..."

# 1. Update MySQL Dialect in any application.properties files
echo "Updating MySQL Dialect in application.properties files..."
find . -name "application.properties" -type f | xargs perl -pi -e 's/org\.marketcetera\.persist\.MarketceteraMySQL5Dialect/org.hibernate.dialect.MySQLDialect/g'

# 2. Update javax.persistence in Spring JPA properties to jakarta.persistence
echo "Updating Spring JPA properties to use jakarta.persistence..."
find . -name "application.properties" -type f | xargs perl -pi -e 's/spring\.jpa\.properties\.javax\.persistence/spring.jpa.properties.jakarta.persistence/g'
find . -name "*.xml" -type f | xargs perl -pi -e 's/<prop key="hibernate\.dialect">.*<\/prop>/<prop key="hibernate.dialect">org.hibernate.dialect.MySQLDialect<\/prop>/g'

# 3. Fix naming strategy which changed in Hibernate 6
echo "Updating Hibernate naming strategy..."
find . -name "application.properties" -type f | xargs perl -pi -e 's/spring\.jpa\.hibernate\.naming_strategy=.*/spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl/g'
find . -name "application.properties" -type f | xargs perl -pi -e 's/spring\.jpa\.hibernate\.naming\.strategy=.*/spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl/g'

# 4. Update HikariCP properties
echo "Updating HikariCP connection pool properties..."
find . -name "application.properties" -type f | xargs perl -pi -e 's/spring\.datasource\.pool-size=/spring.datasource.hikari.maximum-pool-size=/g'
find . -name "application.properties" -type f | xargs perl -pi -e 's/spring\.datasource\.minimum-idle=/spring.datasource.hikari.minimum-idle=/g'
find . -name "application.properties" -type f | xargs perl -pi -e 's/spring\.datasource\.idle-timeout=/spring.datasource.hikari.idle-timeout=/g'

# 5. Add explicit dependency on HikariCP and remove C3P0 dependency where it exists
echo "Updating dependencies for connection pooling..."

# First, detect modules with hibernate-c3p0 dependency
C3P0_MODULES=$(grep -l "hibernate-c3p0" $(find . -name "pom.xml" -type f))

if [[ -n "$C3P0_MODULES" ]]; then
  echo "Modules using C3P0:"
  echo "$C3P0_MODULES"
  
  # For each module using C3P0, add HikariCP instead
  for pom in $C3P0_MODULES; do
    echo "Processing $pom..."
    # 1. Update Hibernate C3P0 dependency with the newer version and proper group ID
    perl -pi -e 's/<dependency>\s*<groupId>org\.hibernate<\/groupId>\s*<artifactId>hibernate-c3p0<\/artifactId>.*<\/dependency>/<dependency>\n      <groupId>org.hibernate.orm<\/groupId>\n      <artifactId>hibernate-hikaricp<\/artifactId>\n      <version>6.2.13.Final<\/version>\n    <\/dependency>/s' "$pom"
    
    # 2. Add HikariCP dependency if not present
    if ! grep -q "HikariCP" "$pom"; then
      sed -i.bak '/<\/dependencies>/i \
    <dependency>\
      <groupId>com.zaxxer</groupId>\
      <artifactId>HikariCP</artifactId>\
      <version>4.0.3</version>\
    </dependency>' "$pom"
      rm -f "${pom}.bak"
    fi
  done
fi

# 6. Update any C3P0 configuration to HikariCP
echo "Searching for C3P0 configuration files to update..."
C3P0_CONFIG_FILES=$(grep -l "hibernate.c3p0" $(find . -name "*.properties" -type f))

if [[ -n "$C3P0_CONFIG_FILES" ]]; then
  echo "C3P0 configuration files:"
  echo "$C3P0_CONFIG_FILES"
  
  for config_file in $C3P0_CONFIG_FILES; do
    echo "Converting C3P0 config to HikariCP in $config_file..."
    # Convert common C3P0 properties to HikariCP equivalents
    perl -pi -e 's/hibernate\.c3p0\.min_size=(\d+)/spring.datasource.hikari.minimum-idle=$1/g' "$config_file"
    perl -pi -e 's/hibernate\.c3p0\.max_size=(\d+)/spring.datasource.hikari.maximum-pool-size=$1/g' "$config_file"
    perl -pi -e 's/hibernate\.c3p0\.timeout=(\d+)/spring.datasource.hikari.idle-timeout=$1/g' "$config_file"
    perl -pi -e 's/hibernate\.c3p0\.max_statements=(\d+)/spring.datasource.hikari.data-source-properties.cachePrepStmts=true\nspring.datasource.hikari.data-source-properties.prepStmtCacheSize=$1/g' "$config_file"
    # Add a comment about the conversion
    sed -i.bak '1i\
# Converted from C3P0 to HikariCP for Spring Boot 3.x compatibility\
' "$config_file"
    rm -f "${config_file}.bak"
  done
fi

echo "Hibernate and connection pool fixes completed!"