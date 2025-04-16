# Jakarta EE Migration for admin-server

This document describes the approach taken to migrate the `admin-server` module to Jakarta EE, particularly focusing on the `ProvisioningAgent` functionality.

## Overview

The `ProvisioningAgent` class uses Spring's XML application context loading and JAR class loading functionality to dynamically provision components at runtime. This functionality has been fully migrated from Java EE (javax.*) to Jakarta EE (jakarta.*) APIs.

## Migration Approach

1. **Direct Jakarta EE Usage**: 
   - Removed all compatibility layers and compatibility checks
   - Use Jakarta EE APIs directly (`jakarta.annotation.*`, etc.)
   - Configure system properties directly in test classes

2. **Test Resources**:
   - Rebuilt test JAR with Jakarta EE dependencies
   - Ensured XML files use Jakarta EE compatible schemas
   - Set appropriate system properties for XML parsing

3. **Dependency Management**:
   - Use Spring Boot's dependency management for Jakarta EE
   - Added Eclipse MOXy for JAXB implementation
   - Removed any references to Java EE (javax.*) APIs

## System Properties

Essential Jakarta EE properties set for tests:

```java
// JAXB Implementation
System.setProperty("jakarta.xml.bind.JAXBContextFactory", 
        "org.eclipse.persistence.jaxb.JAXBContextFactory");

// Spring Framework compatibility
System.setProperty("spring.classloader.jakarta-compatible", "true");

// XML Parser settings
System.setProperty("jakarta.xml.accessExternalDTD", "all");
```

## Lessons Learned

1. **Direct Migration**: It's better to migrate directly to Jakarta EE rather than creating compatibility layers
2. **Eliminate Compatibility Checks**: Don't try to check for both Java EE and Jakarta EE classes
3. **Rebuild Test Resources**: Any JAR files used for testing should be rebuilt with Jakarta EE dependencies
4. **System Properties**: Key system properties must be set to ensure XML processing works correctly

## Future Work

1. **Verify Other Modules**: Ensure all modules that interact with this one also use Jakarta EE
2. **Simplify Dynamic Loading**: Consider simplifying the dynamic class loading approach
3. **Modern Alternatives**: Evaluate Spring Boot auto-configuration as an alternative to dynamic XML loading