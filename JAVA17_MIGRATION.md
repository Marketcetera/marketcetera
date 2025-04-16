# Java 17 Migration Guide

This project has been upgraded from Java 11 to Java 17. This document provides information about the migration and how to work with the codebase using Java 17.

## Requirements

- Java 17 JDK (Temurin/Eclipse Adoptium recommended)
- Maven 3.8.x or newer

## Building with Java 17

### Using the Helper Script

A helper script has been provided to build the project with Java 17:

```bash
./build-with-java17.sh [maven arguments]
```

This script sets the `JAVA_HOME` environment variable to point to the Java 17 installation and then runs Maven with the specified arguments.

### Manual Build

To manually build with Java 17:

1. Set your `JAVA_HOME` environment variable to point to your Java 17 installation:

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home
```

2. Run Maven as usual:

```bash
mvn clean install
```

## Java 17 Configuration

The project is configured to use Java 17 with the following settings:

- Maven compiler plugin configured to use Java 17 (source and target)
- Required JVM module flags for Java 17 compatibility:
  - `--add-exports=java.base/sun.nio.ch=ALL-UNNAMED`
  - `--add-opens=java.base/java.lang=ALL-UNNAMED`
  - `--add-opens=java.base/java.lang.reflect=ALL-UNNAMED`
  - `--add-opens=java.base/java.io=ALL-UNNAMED`
  - `--add-opens=java.base/java.util=ALL-UNNAMED`

## Notes on Java 17 Features

Java 17 includes several new features and improvements over Java 11:

- Pattern matching for switch (preview)
- Sealed classes (finalized)
- Records (finalized)
- Text blocks (finalized)
- Helpful NullPointerExceptions
- Foreign Function & Memory API (Incubator)
- Vector API (Second Incubator)

For a complete list of Java 17 features, see the [JDK 17 Release Notes](https://www.oracle.com/java/technologies/javase/17-relnote-issues.html).

## Known Issues and Resolved Problems

During the migration, we encountered and fixed the following issues:

1. **Removed APIs**: 
   - `java.rmi.activation` package has been removed in Java 17. We replaced the usage of `ActivationException` and `UnknownObjectException` in tests with `RemoteException` and `NotBoundException`.

2. **Changed internals**: 
   - Updated reflection class references in tests as the internal implementation classes have changed in Java 17 
   - Updated `jdk.internal.reflect.NativeConstructorAccessorImpl` to `jdk.internal.reflect.DirectConstructorHandleAccessor`

3. **Deprecation warnings**: 
   - Some code generates deprecation warnings with Java 17. This is normal as some APIs have been deprecated between Java 11 and Java 17. These should be addressed in future updates.
   - For example, `Long(long)` constructor is deprecated and marked for removal in the core module.

4. **Jakarta EE Migration**:
   - Migrated from `javax.*` packages to `jakarta.*` packages for Java EE APIs
   - Updated JAXB implementations to work with Jakarta EE in:
     - `admin-server` module
     - `cluster-core` module
   - Added explicit Jakarta EE compatible JAXB dependencies:
     - `jakarta.xml.bind-api:4.0.0`
     - `org.eclipse.persistence.moxy:4.0.0`
     - `jaxb-runtime:4.0.2`
   - See the `JAKARTA_EE.md` files in the affected modules for details

## IDE Configuration

If you're using Eclipse, IntelliJ IDEA, or another IDE, make sure to configure it to use Java 17 for this project.