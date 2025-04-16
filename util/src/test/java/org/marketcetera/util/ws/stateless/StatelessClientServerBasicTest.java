package org.marketcetera.util.ws.stateless;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.compatibility.JakartaCompatibilitySetup;
import org.marketcetera.util.ws.compatibility.JakartaCxfHelper;
import org.marketcetera.util.ws.tags.AppId;

/**
 * Test only the basic client-server functionality that works with Jakarta EE.
 * This is a subset of the tests in StatelessClientServerTest that are compatible
 * with Jakarta EE constraints.
 * 
 * @author claude
 */
public class StatelessClientServerBasicTest {
    
    private static final String TEST_HOST = "localhost";
    private static final int TEST_PORT = 9002;
    private static final AppId TEST_APP = new AppId("testApp");
    
    @BeforeClass
    public static void setupJakartaEE() {
        // Initialize Jakarta EE compatibility settings
        JakartaCompatibilitySetup.setupJakartaCompatibility();
        
        // Pre-configure the test port
        JakartaCxfHelper.configureJettyForPort(TEST_PORT);
        
        SLF4JLoggerProxy.debug(StatelessClientServerBasicTest.class, 
                "Jakarta EE compatibility setup complete for basic tests");
    }
    
    /**
     * Test that a client can be created with the application ID.
     */
    @Test
    public void clientCreation() {
        try {
            StatelessClient client = new StatelessClient(TEST_HOST, TEST_PORT, TEST_APP);
            StatelessClientContext context = client.getContext();
            
            // Verify client was created properly
            SLF4JLoggerProxy.debug(this, "Client context: {}", context);
            assert(client.getAppId() == TEST_APP);
            assert(context.getVersionId() != null);
            assert(context.getAppId() == TEST_APP);
            assert(context.getClientId() != null);
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this, e, "Client creation test failed: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Test that a server can be created and stopped.
     */
    @Test
    public void serverLifecycle() {
        try {
            // Create server
            StatelessServer server = new StatelessServer(TEST_HOST, TEST_PORT);
            
            // Server properties should be set
            assert(server.getHost().equals(TEST_HOST));
            assert(server.getPort() == TEST_PORT);
            
            // Stop server
            server.stop();
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this, e, "Server lifecycle test failed: {}", e.getMessage());
            throw e;
        }
    }
}