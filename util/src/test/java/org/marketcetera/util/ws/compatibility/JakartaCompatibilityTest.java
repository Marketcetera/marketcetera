package org.marketcetera.util.ws.compatibility;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateless.StatelessClient;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.stateless.StatelessServer;
import org.marketcetera.util.ws.stateless.StatelessServiceBase;
import org.marketcetera.util.ws.tags.AppId;

import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceException;

/**
 * Test class to verify Jakarta EE compatibility setup works correctly.
 */
public class JakartaCompatibilityTest {

    private static final String TEST_HOST = "localhost";
    private static final int TEST_PORT = 9001;
    private static final AppId TEST_APP = new AppId("testApp");
    
    private StatelessServer server;
    private StatelessClient client;
    
    @WebService
    public interface TestService extends StatelessServiceBase {
        String echo(StatelessClientContext context, String message);
    }
    
    public static class TestServiceImpl implements TestService {
        @Override
        public String echo(StatelessClientContext context, String message) {
            return "ECHO: " + message;
        }
    }

    @Before
    public void setUp() {
        // Initialize Jakarta EE compatibility settings
        JakartaCompatibilitySetup.setupJakartaCompatibility();
        
        // Set up server and client
        server = new StatelessServer(TEST_HOST, TEST_PORT);
        client = new StatelessClient(TEST_HOST, TEST_PORT, TEST_APP);
        
        // Configure Jetty for the test port
        JakartaCxfHelper.configureJettyForPort(TEST_PORT);
    }
    
    @After
    public void tearDown() {
        if (server != null) {
            server.stop();
        }
    }
    
    @Test
    public void testHelperClasses() {
        // Verify helper methods work
        JaxWsServerFactoryBean serverFactory = new JaxWsServerFactoryBean();
        JaxWsProxyFactoryBean clientFactory = new JaxWsProxyFactoryBean();
        
        JakartaCxfHelper.configureServerFactory(serverFactory, false);
        JakartaCxfHelper.configureClientFactory(clientFactory, false);
        
        assertNotNull("Server factory should be configured", serverFactory);
        assertNotNull("Client factory should be configured", clientFactory);
    }
    
    @Test
    public void testServicePublishAndCall() {
        try {
            // Publish the service
            server.publish(new TestServiceImpl(), TestService.class);
            
            // Get a client proxy
            TestService service = client.getService(TestService.class);
            
            // Make a call
            String result = service.echo(client.getContext(), "Hello Jakarta EE");
            
            assertNotNull("Service call should return a result", result);
            assertTrue("Result should contain the echoed message", 
                    result.contains("Hello Jakarta EE"));
            
            SLF4JLoggerProxy.info(this, "Service call successful: {}", result);
        } catch (WebServiceException e) {
            SLF4JLoggerProxy.warn(this, e, "Service call failed: {}", e.getMessage());
            throw e;
        }
    }
}