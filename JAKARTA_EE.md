# Jakarta EE Migration

This project has been fully migrated from Java EE (javax.*) to Jakarta EE (jakarta.*).

## Migrated Components

The following Java EE components have been migrated to their Jakarta EE equivalents:

1. **Jakarta Persistence (JPA)**
   - Migrated from `javax.persistence` to `jakarta.persistence`
   - Updated JPA implementations to work with Jakarta EE standards
   - Updated all entity classes, repositories, and specifications

2. **Jakarta XML Binding (JAXB)**
   - Migrated from `javax.xml.bind` to `jakarta.xml.bind`
   - Added Jakarta EE compatible JAXB implementations:
     - `jakarta.xml.bind-api:4.0.0`
     - `org.eclipse.persistence.moxy:4.0.0`
     - `jaxb-runtime:4.0.2`

3. **Jakarta Annotations**
   - Migrated from `javax.annotation` to `jakarta.annotation`
   - Updated all annotation imports in service classes

4. **Jakarta Validation**
   - Migrated from `javax.validation` to `jakarta.validation`
   - Updated all validator implementations

5. **Jakarta Mail**
   - Migrated from `javax.mail` to `jakarta.mail`
   - Upgraded to Jakarta Mail API 2.1.2

6. **Jakarta Servlet**
   - Migrated from `javax.servlet` to `jakarta.servlet`
   - Updated Spring Boot to 3.x which uses Jakarta EE

7. **Jakarta Transaction**
   - Migrated from `javax.transaction` to `jakarta.transaction`
   - Updated transaction annotations across the codebase

## Implementation Notes

1. **Module-Specific Documentation**
   - See `JAKARTA_EE.md` files in the following modules for specific implementation details:
     - `admin-server`
     - `cluster-core`

2. **System Properties**
   - When running tests, the following system properties may be needed:
     - `-Djakarta.xml.bind.JAXBContextFactory=org.eclipse.persistence.jaxb.JAXBContextFactory`
     - `-Djavax.xml.accessExternalDTD=all`
     - `-Djavax.xml.accessExternalSchema=all`

3. **Test Scripts**
   - Use the provided test scripts in the `tools/scripts` directory to run tests with the proper Jakarta EE settings:
     - `test-jakarta-jaxb.sh`
     - `test-cluster-jakarta-jaxb.sh`

## Importance

The migration to Jakarta EE is essential because:

1. Java EE is no longer being actively developed under the `javax.*` namespace
2. Jakarta EE is the future path for enterprise Java development
3. Modern frameworks like Spring Boot 3.x require Jakarta EE
4. Mixing both Jakarta EE and Java EE can lead to compatibility issues and classloading conflicts

## Backward Compatibility Considerations

This project has been completely migrated to Jakarta EE. However, there are some backward compatibility considerations:

1. **gRPC Generated Code**: The gRPC code generators currently produce code that uses `javax.annotation` for things like `@Generated` annotations. To handle this, we've kept `javax.annotation-api` as a provided dependency in the RPC proto modules. This dependency is marked with `provided` scope to ensure it doesn't leak into runtime environments.

2. **Dependency Management**: All Java EE dependencies in the parent POM have been replaced with Jakarta EE equivalents. Any references to `javax.*` packages in code have been updated to use `jakarta.*`.

3. **Future Upgrades**: When upgrading gRPC or other dependencies that generate code, check if they support Jakarta EE natively and remove any remaining `javax` dependencies accordingly.