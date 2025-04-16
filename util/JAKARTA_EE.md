# Jakarta EE Compatibility in JAX-WS Components

This document describes the Jakarta EE compatibility implementation for JAX-WS components in the `util` module.

## Overview

The JAX-WS components in the `util` module have been updated to work with Jakarta EE 9+ (using the `jakarta.*` namespace instead of `javax.*`). This is necessary for compatibility with newer Java EE/Jakarta EE versions and Spring Boot 3.x.

## Implementation Details

The compatibility solution includes:

1. **Helper Classes**:
   - `JakartaCompatibilitySetup`: Sets up system properties and configuration for tests
   - `JakartaCxfHelper`: Provides utility methods to configure CXF clients and servers

2. **Configuration Files**:
   - `jakarta-compatibility.properties`: Contains necessary system properties for Jakarta EE compatibility

3. **Testing Infrastructure**:
   - `JakartaCompatibilityTest`: Tests basic Jakarta EE compatibility
   - `StatelessClientServerBasicTest`: A subset of stateless client-server tests compatible with Jakarta EE
   - `ClientServerBasicTest`: A subset of stateful client-server tests compatible with Jakarta EE
   - `ClientServerTest`: Full stateful client-server test adapted for Jakarta EE
   - Maven profile: `jakarta-compatible` to run only compatible tests
   - Test script: `tools/scripts/test-jaxws-compatibility.sh`

4. **Runtime Adaptation**:
   - Modified `StatelessServer` and `StatelessClient` to use compatibility helpers
   - Added reflection-based feature detection to allow code to run in both Jakarta EE and legacy environments

## Dependencies

The following dependencies have been updated:

```xml
<!-- Jakarta EE APIs -->
<dependency>
  <groupId>jakarta.xml.ws</groupId>
  <artifactId>jakarta.xml.ws-api</artifactId>
  <version>4.0.0</version>
</dependency>
<dependency>
  <groupId>jakarta.servlet</groupId>
  <artifactId>jakarta.servlet-api</artifactId>
  <version>5.0.0</version>
</dependency>
<dependency>
  <groupId>jakarta.annotation</groupId>
  <artifactId>jakarta.annotation-api</artifactId>
  <version>2.1.1</version>
</dependency>
<dependency>
  <groupId>jakarta.xml.bind</groupId>
  <artifactId>jakarta.xml.bind-api</artifactId>
  <version>4.0.0</version>
</dependency>
<dependency>
  <groupId>jakarta.jws</groupId>
  <artifactId>jakarta.jws-api</artifactId>
  <version>3.0.0</version>
</dependency>

<!-- JAXWS implementation -->
<dependency>
  <groupId>com.sun.xml.ws</groupId>
  <artifactId>jaxws-rt</artifactId>
  <version>4.0.1</version>
</dependency>

<!-- CXF with Jetty for testing -->
<dependency>
  <groupId>org.apache.cxf</groupId>
  <artifactId>cxf-rt-frontend-jaxws</artifactId>
  <version>4.0.3</version>
</dependency>
<dependency>
  <groupId>org.apache.cxf</groupId>
  <artifactId>cxf-rt-transports-http-jetty</artifactId>
  <version>4.0.3</version>
</dependency>

<!-- Jetty dependencies compatible with Jakarta Servlet 5.0 -->
<dependency>
  <groupId>org.eclipse.jetty</groupId>
  <artifactId>jetty-server</artifactId>
  <version>11.0.18</version>
</dependency>
```

## How to Use

### Running Compatible Tests

To run the JAX-WS tests that are compatible with Jakarta EE:

```bash
# Option 1: Run with Maven profile
mvn test -pl util -P jakarta-compatible

# Option 2: Use the test script
./tools/scripts/test-jaxws-compatibility.sh
```

### Implementing Jakarta EE Compatible Components

To make a JAX-WS component compatible with Jakarta EE:

1. Add the `JakartaCompatibilitySetup` class to your test setup:

```java
@BeforeClass
public static void setupJakartaEE() {
    // Initialize Jakarta EE compatibility settings
    JakartaCompatibilitySetup.setupJakartaCompatibility();
}
```

2. Use the `JakartaCxfHelper` to configure CXF components:

```java
// Configure a server factory
JaxWsServerFactoryBean serverFactory = new JaxWsServerFactoryBean();
JakartaCxfHelper.configureServerFactory(serverFactory, false);

// Configure a client factory
JaxWsProxyFactoryBean clientFactory = new JaxWsProxyFactoryBean();
JakartaCxfHelper.configureClientFactory(clientFactory, false);

// Configure Jetty for a specific port
JakartaCxfHelper.configureJettyForPort(9001);
```

## Known Limitations

1. Not all JAX-WS tests are compatible with Jakarta EE yet. Some tests use features that are not fully compatible with the Jakarta EE version implemented.

2. The `badConnection` test in `StatelessClientServerTest` is skipped when using Jakarta EE compatibility mode due to different exception handling.

3. For maximum compatibility, avoid using negative port numbers in tests, as they cause different exceptions in CXF 4.0.3.

## Future Work

1. Refactor remaining incompatible tests to work with Jakarta EE

2. Further isolate Jakarta EE-specific code to maintain compatibility with both legacy and modern environments

3. Consider moving JAX-WS code to a dedicated module that can be optionally included

## References

- [Jakarta XML Binding Specification](https://jakarta.ee/specifications/xml-binding/)
- [Jakarta EE 9 Migration Guide](https://eclipse-ee4j.github.io/jakartaee-tutorial/#jakarta-ee-9-migration-guide)
- [Apache CXF Documentation](https://cxf.apache.org/docs/index.html)