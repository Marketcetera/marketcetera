# Jakarta EE Migration Notes for Cluster Core

## The Problem

The `AbstractClusterService` class in cluster-core has a static initializer that uses JAXB:

```java
static {
    try {
        context = JAXBContext.newInstance(SimpleClusterMetaData.class);
        marshaller = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();
        unmarshaller.setEventHandler(new ValidationEventHandler() {
            @Override
            public boolean handleEvent(ValidationEvent inEvent) {
                throw new RuntimeException(inEvent.getMessage(),
                                           inEvent.getLinkedException());
            }
        });
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException(e);
    }
}
```

This static initializer attempts to create a JAXBContext using the jakarta.xml.bind package, which is the newer Jakarta EE API. However, the runtime implementation was still trying to use the older javax.xml.bind implementation internally, causing class loading conflicts and NoClassDefFoundError exceptions.

## The Solution

The solution is to ensure that we have the proper Jakarta EE compatible JAXB implementations available:

1. Added explicit dependencies on Jakarta EE JAXB implementation:
   - `jakarta.xml.bind-api:4.0.0`: The Jakarta EE XML Binding API
   - `org.eclipse.persistence.moxy:4.0.0`: The EclipseLink MOXy implementation
   - `jaxb-runtime:4.0.2`: The Glassfish JAXB Runtime

2. System properties that may be needed at runtime to ensure proper implementation selection:
   - `-Djakarta.xml.bind.JAXBContextFactory=org.eclipse.persistence.jaxb.JAXBContextFactory`
   - `-Djavax.xml.accessExternalDTD=all`
   - `-Djavax.xml.accessExternalSchema=all`

## Testing

When testing applications that use cluster-core, especially in a Spring Boot context, make sure to:

1. Include these dependencies in your test scope
2. Set the appropriate system properties in your test configuration or command line
3. Consider using this pattern to configure JAXB in tests:

```java
@BeforeClass
public static void setupJaxb() {
    System.setProperty("jakarta.xml.bind.JAXBContextFactory", 
                      "org.eclipse.persistence.jaxb.JAXBContextFactory");
}
```

## Root Cause Analysis

The underlying issue is that the Jakarta JAXB implementation's `ContextFinder` class was trying to use reflection to find implementations that use the older `javax.xml.bind` packages, causing class loading issues when those classes are not available.

By explicitly including the Jakarta-compatible implementations and setting the appropriate factory, we ensure that the proper implementation is used.