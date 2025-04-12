# Database Properties Configuration

This document explains how database properties are configured for CI/CD builds in this project.

## Properties Configuration in CI/CD

For CI/CD builds, database properties are provided through environment variables instead of using Maven's `settings.xml` file. This approach is more secure and follows CI/CD best practices.

### Environment Variables Used

The following environment variables are used:

| Environment Variable    | Maven Property           | Description                    |
|-------------------------|--------------------------|--------------------------------|
| METC_JDBC_PASSWORD      | metc.jdbc.password       | Database password              |
| METC_JDBC_DRIVER        | metc.jdbc.driver         | JDBC driver class              |
| METC_JDBC_URL           | metc.jdbc.url            | JDBC connection URL            |
| METC_JDBC_TESTQUERY     | metc.jdbc.testquery      | Connection test query          |
| METC_HIBERNATE_DIALECT  | metc.hibernate.dialect   | Hibernate dialect class        |
| METC_FLYWAY_VENDOR      | metc.flyway.vendor       | Flyway database vendor         |

### CI/CD Implementation

In GitHub Actions workflows, these environment variables are:

1. Defined in the workflow file directly
2. Passed to Maven as system properties using the `-D` flag

Example:
```yaml
env:
  METC_JDBC_PASSWORD: pw4metc
  METC_JDBC_DRIVER: org.h2.Driver
  METC_JDBC_URL: jdbc:h2:./target/metc
  METC_JDBC_TESTQUERY: select 1
  METC_HIBERNATE_DIALECT: org.hibernate.dialect.H2Dialect
  METC_FLYWAY_VENDOR: h2

run: |
  mvn -B package --file pom.xml \
    -Dmetc.jdbc.password=${METC_JDBC_PASSWORD} \
    -Dmetc.jdbc.driver=${METC_JDBC_DRIVER} \
    -Dmetc.jdbc.url=${METC_JDBC_URL} \
    -Dmetc.jdbc.testquery=${METC_JDBC_TESTQUERY} \
    -Dmetc.hibernate.dialect=${METC_HIBERNATE_DIALECT} \
    -Dmetc.flyway.vendor=${METC_FLYWAY_VENDOR}
```

## Local Development

For local development, these properties are typically provided in one of these ways:

1. Maven `settings.xml` file (traditional approach)
2. Environment variables
3. Application properties files

### Example `settings.xml` Configuration

```xml
<profile>
  <id>metc-jdbc</id>
  <properties>
    <metc.jdbc.password>pw4metc</metc.jdbc.password>
    <metc.jdbc.driver>org.h2.Driver</metc.jdbc.driver>
    <metc.jdbc.url>jdbc:h2:./target/metc</metc.jdbc.url>
    <metc.jdbc.testquery>select 1</metc.jdbc.testquery>
    <metc.hibernate.dialect>org.hibernate.dialect.H2Dialect</metc.hibernate.dialect>
    <metc.flyway.vendor>h2</metc.flyway.vendor>
  </properties>
</profile>
```

## Helper Scripts

This repository includes helper scripts for working with these properties:

1. `verify-env.sh` - Verifies that all required environment variables are set
2. `property-resolver.sh` - Converts environment variables to Maven properties

These scripts are primarily used in CI workflows but can also be helpful for local development and testing.