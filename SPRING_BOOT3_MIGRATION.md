# Spring Boot 3.x Migration Guide

This document explains the migration from Spring Boot 2.7.18 to Spring Boot 3.2.5 that was performed on the Marketcetera platform.

## Major Changes

1. **Jakarta EE Instead of Java EE**:
   - All `javax.*` packages renamed to `jakarta.*`
   - This affects JPA, Servlet, JMS, and other Java EE APIs
   
2. **Hibernate 6.x**:
   - Updated from Hibernate 5.6.x to Hibernate 6.2.x
   - JPA 3.1 implementation with Jakarta EE support
   - New dialect naming and configuration
   
3. **MySQL Dialect**:
   - Updated `MarketceteraMySQL5Dialect` to extend `MySQLDialect` and provide database version
   - Changed connection pool from C3P0 to HikariCP
   
4. **QueryDSL 5.x**:
   - Using QueryDSL 5.0.0 with Jakarta EE support
   - Using the Maven compiler plugin for annotation processing instead of apt-maven-plugin
   - Using `jakarta` classifier for QueryDSL dependencies

## Migration Scripts

The migration was automated with several scripts:

1. `migration_jakarta_persistence.sh`: Migrates javax.persistence imports to jakarta.persistence
2. `fix_querydsl_imports.sh`: Fixes QueryDSL generated files to use Jakarta imports
3. `hibernate_spring_boot3_fixes.sh`: Updates Hibernate dialect and connection pool settings
4. `spring_boot3_jakarta_migration.sh`: Master script that runs all the other scripts

## Dependencies Updated

The following dependencies were updated:

```xml
<!-- Spring Boot updated to 3.2.5 -->
<mvn.spring.boot.version>3.2.5</mvn.spring.boot.version>

<!-- CXF updated to 4.0.4 -->
<mvn.cxf.version>4.0.4</mvn.cxf.version>

<!-- QueryDSL 5.0.0 for Jakarta EE support -->
<querydsl.version>5.0.0</querydsl.version>

<!-- Jakarta EE dependencies -->
<dependency>
  <groupId>jakarta.annotation</groupId>
  <artifactId>jakarta.annotation-api</artifactId>
  <version>2.1.1</version>
</dependency>
<dependency>
  <groupId>jakarta.persistence</groupId>
  <artifactId>jakarta.persistence-api</artifactId>
  <version>3.1.0</version>
</dependency>
<dependency>
  <groupId>jakarta.xml.bind</groupId>
  <artifactId>jakarta.xml.bind-api</artifactId>
  <version>4.0.0</version>
</dependency>
<dependency>
  <groupId>jakarta.servlet</groupId>
  <artifactId>jakarta.servlet-api</artifactId>
  <version>6.0.0</version>
</dependency>
```

## QueryDSL Configuration

The QueryDSL annotation processing was configured in the maven-compiler-plugin:

```xml
<plugin>
  <artifactId>maven-compiler-plugin</artifactId>
  <configuration>
    <annotationProcessorPaths>
      <path>
        <groupId>com.querydsl</groupId>
        <artifactId>querydsl-apt</artifactId>
        <version>${querydsl.version}</version>
        <classifier>jakarta</classifier>
      </path>
      <path>
        <groupId>jakarta.persistence</groupId>
        <artifactId>jakarta.persistence-api</artifactId>
        <version>3.1.0</version>
      </path>
    </annotationProcessorPaths>
    <annotationProcessors>
      <annotationProcessor>com.querydsl.apt.jpa.JPAAnnotationProcessor</annotationProcessor>
    </annotationProcessors>
    <compilerArgs>
      <arg>-Aquerydsl.entityAccessors=true</arg>
      <arg>-Aquerydsl.packageSuffix=</arg>
    </compilerArgs>
  </configuration>
</plugin>
```

## Known Issues and Solutions

1. **EntityManager not found**:
   - Problem: QueryDSL generated files still using javax.persistence imports
   - Solution: Run `fix_querydsl_imports.sh` to update imports in generated files

2. **MySQL Dialect issues**:
   - Problem: MarketceteraMySQL5Dialect needs updates for Hibernate 6
   - Solution: Updated to extend MySQLDialect with DatabaseVersion

3. **Connection Pool issues**:
   - Problem: C3P0 configuration not compatible with Spring Boot 3.x
   - Solution: Migrated to HikariCP which is the default in Spring Boot

4. **Concurrent Annotations**:
   - Problem: javax.annotation.concurrent packages no longer available
   - Solution: Replaced with net.jcip.annotations
   
5. **HTTP Client**:
   - Problem: Apache HttpClient 4.x not compatible with Jakarta EE
   - Solution: Updated to Apache HttpClient 5.2.3
   
6. **SpringDoc OpenAPI**:
   - Problem: springdoc-openapi-ui not compatible with Spring Boot 3
   - Solution: Updated to springdoc-openapi-starter-webmvc-ui 2.4.0

## Testing Recommendations

1. Test database connectivity and operations
2. Test all REST endpoints
3. Test FIX message handling
4. Test cluster operations
5. Test admin functionality
6. Run integration tests

## References

1. [Spring Boot 3.2 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide)
2. [Hibernate ORM 6.2 Migration Guide](https://github.com/hibernate/hibernate-orm/blob/6.2/migration-guide.adoc)
3. [Jakarta EE 10 Documentation](https://jakarta.ee/specifications/platform/10/)
4. [QueryDSL 5.0 Documentation](http://querydsl.com/)