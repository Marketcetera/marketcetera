package org.marketcetera.util.ws.compatibility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.BeforeClass;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/**
 * Helper class to set up Jakarta EE compatibility for web service tests.
 * This class configures necessary system properties to ensure CXF and Jetty
 * work properly with Jakarta EE 9+ APIs.
 * 
 * @author claude
 */
public class JakartaCompatibilitySetup {
    
    /**
     * Sets up system properties for Jakarta EE compatibility before tests run
     */
    @BeforeClass
    public static void setupJakartaCompatibility() {
        Properties props = new Properties();
        try (InputStream is = JakartaCompatibilitySetup.class.getResourceAsStream(
                "/org/marketcetera/util/ws/compatibility/jakarta-compatibility.properties")) {
            if (is != null) {
                props.load(is);
                for (String name : props.stringPropertyNames()) {
                    if (name.startsWith("org.")) {
                        System.setProperty(name, props.getProperty(name));
                        SLF4JLoggerProxy.debug(JakartaCompatibilitySetup.class, 
                                "Setting system property: {}={}", name, props.getProperty(name));
                    }
                }
            } else {
                SLF4JLoggerProxy.debug(JakartaCompatibilitySetup.class, 
                        "jakarta-compatibility.properties not found in classpath");
            }
        } catch (IOException e) {
            SLF4JLoggerProxy.warn(JakartaCompatibilitySetup.class, 
                    e, "Error loading Jakarta EE compatibility properties");
        }
        
        // Essential property for CXF with Jakarta EE
        System.setProperty("org.apache.cxf.stax.allowInsecureParser", "1");
        
        // Add other properties as needed for Jetty/CXF compatibility
        System.setProperty("org.eclipse.jetty.server.Request.maxFormKeys", "1000");
    }
}