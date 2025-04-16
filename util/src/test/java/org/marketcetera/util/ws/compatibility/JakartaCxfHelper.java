package org.marketcetera.util.ws.compatibility;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngine;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngineFactory;
import org.apache.cxf.transport.http_jetty.ThreadingParameters;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper utilities for working with CXF in Jakarta EE environments.
 * This class provides methods to configure CXF services and clients
 * to work correctly with Jakarta EE 9+ APIs.
 *
 * @author claude
 */
public class JakartaCxfHelper {

    /**
     * Sets up a CXF JAX-WS server factory with Jakarta EE compatibility settings.
     *
     * @param serverFactory The server factory to configure
     * @param enableLogging Whether to enable verbose CXF logging
     * @return The configured server factory
     */
    public static JaxWsServerFactoryBean configureServerFactory(
            JaxWsServerFactoryBean serverFactory, boolean enableLogging) {
        
        // Add properties for Jakarta EE compatibility
        Map<String, Object> props = serverFactory.getProperties();
        if (props == null) {
            props = new HashMap<>();
        }
        
        // Set properties needed for Jakarta EE compatibility
        props.put("jaxb.additionalContextClasses", new Class[0]);
        props.put("org.apache.cxf.stax.allowInsecureParser", Boolean.TRUE);
        
        serverFactory.setProperties(props);
        
        // Add standard system properties for Jakarta compatibility
        System.setProperty("org.apache.cxf.stax.allowInsecureParser", "1");
        
        return serverFactory;
    }
    
    /**
     * Sets up a CXF JAX-WS client proxy factory with Jakarta EE compatibility settings.
     *
     * @param proxyFactory The proxy factory to configure
     * @param enableLogging Whether to enable verbose CXF logging
     * @return The configured proxy factory
     */
    public static JaxWsProxyFactoryBean configureClientFactory(
            JaxWsProxyFactoryBean proxyFactory, boolean enableLogging) {
        
        // Add properties for Jakarta EE compatibility
        Map<String, Object> props = proxyFactory.getProperties();
        if (props == null) {
            props = new HashMap<>();
        }
        
        // Set properties needed for Jakarta EE compatibility
        props.put("jaxb.additionalContextClasses", new Class[0]);
        props.put("org.apache.cxf.stax.allowInsecureParser", Boolean.TRUE);
        
        proxyFactory.setProperties(props);
        
        // Add standard system properties for Jakarta compatibility
        System.setProperty("org.apache.cxf.stax.allowInsecureParser", "1");
        
        return proxyFactory;
    }
    
    /**
     * Configures Jetty HTTP server engine settings for Jakarta EE compatibility.
     * 
     * @param port The port to configure
     */
    public static void configureJettyForPort(int port) {
        try {
            JettyHTTPServerEngineFactory factory = new JettyHTTPServerEngineFactory();
            
            // Get or create engine for the port
            JettyHTTPServerEngine engine = factory.retrieveJettyHTTPServerEngine(port);
            if (engine == null) {
                // Create a new engine if one doesn't exist
                factory.createJettyHTTPServerEngine(port, "http");
                engine = factory.retrieveJettyHTTPServerEngine(port);
            }
            
            // Configure engine properties for Jakarta EE compatibility
            if (engine != null) {
                ThreadingParameters threadParams = new ThreadingParameters();
                threadParams.setMinThreads(5);
                threadParams.setMaxThreads(30);
                engine.setThreadingParameters(threadParams);
                
                // Send server version header
                engine.setReuseAddress(true);
                engine.setSendServerVersion(false);
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(JakartaCxfHelper.class, 
                    e, "Error configuring Jetty for port {}", port);
        }
    }
}