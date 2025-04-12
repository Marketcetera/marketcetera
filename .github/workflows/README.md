# GitHub CI for marketcetera

This directory contains GitHub Actions workflow configurations for continuous integration of the marketcetera project.

## CI Workflow (`ci.yml`)

The `ci.yml` workflow handles:

- Building the project with Maven
- Running tests
- Publishing test reports
- Uploading build artifacts

### Environment Variables

The CI workflow sets the following environment variables for database configuration:

```
METC_JDBC_PASSWORD=pw4metc
METC_JDBC_DRIVER=org.h2.Driver
METC_JDBC_URL=jdbc:h2:./target/metc
METC_JDBC_TESTQUERY=select 1
METC_HIBERNATE_DIALECT=org.hibernate.dialect.H2Dialect
METC_FLYWAY_VENDOR=h2
```

These environment variables replace the properties that would typically be defined in a `settings.xml` file.

## Trigger Events

The workflow runs on:
- Push to branches: main, master, develop, java17-upgrade
- Pull requests targeting branches: main, master, develop

## Build Artifacts

After successful builds, the following artifacts are uploaded:
- JAR files (excluding source and test JARs)
- WAR files

These can be downloaded from the GitHub Actions workflow summary page.