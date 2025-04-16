package org.marketcetera.util.ws.stateful;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.compatibility.JakartaCompatibilitySetup;
import org.marketcetera.util.ws.compatibility.JakartaCxfHelper;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.util.ws.tags.SessionId;
import org.marketcetera.util.ws.wrappers.RemoteException;

import jakarta.xml.ws.WebServiceException;

/**
 * Basic tests for stateful client-server functionality with Jakarta EE compatibility.
 * This is a simplified version of ClientServerTest that works with Jakarta EE.
 * 
 * @author claude
 */
public class ClientServerBasicTest {

    // Test constants
    private static final String TEST_HOST = "localhost";
    private static final int TEST_PORT = 9003;
    private static final AppId TEST_APP = new AppId("testApp");
    private static final Authenticator TEST_AUTHENTICATOR = new FixedAuthenticator();
    // We use an instance variable for the session manager instead of a static one
    private static final String TEST_USER = "metc";
    private static final String TEST_USER_D = "metcD";
    private static final char[] TEST_PASSWORD = "metc".toCharArray();

    @BeforeClass
    public static void setupJakartaEE() {
        // Initialize Jakarta EE compatibility settings
        JakartaCompatibilitySetup.setupJakartaCompatibility();
        
        // Pre-configure the test port
        JakartaCxfHelper.configureJettyForPort(TEST_PORT);
        JakartaCxfHelper.configureJettyForPort(TEST_PORT + 1);
        
        SLF4JLoggerProxy.debug(ClientServerBasicTest.class, 
                "Jakarta EE compatibility setup complete for stateful client-server tests");
    }
    
    // We use a new session manager for each test to avoid session conflicts
    private final SessionManager<Object> testManager = new SessionManager<Object>();
    
    @Before
    public void setupTest() {
        // Nothing needed, we use a fresh session manager for each test
    }
    
    private static StatelessClientContext getStatelessContext(Client client) {
        ClientContext context = client.getContext();
        StatelessClientContext statelessContext = new StatelessClientContext();
        statelessContext.setVersionId(context.getVersionId());
        statelessContext.setAppId(context.getAppId());
        statelessContext.setClientId(context.getClientId());
        statelessContext.setLocale(context.getLocale());
        return statelessContext;
    }

    /**
     * Test basic client creation and properties
     */
    @Test
    public void clientCreation() {
        try {
            Client client = new Client(TEST_HOST, TEST_PORT, TEST_APP);
            
            // Verify client was created properly
            assertEquals(TEST_HOST, client.getHost());
            assertEquals(TEST_PORT, client.getPort());
            assertEquals(TEST_APP, client.getAppId());
            assertNotNull(client.getContext());
            assertNull(client.getSessionId());
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this, e, "Client creation test failed: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Test server creation and lifecycle
     */
    @Test
    public void serverLifecycle() {
        try {
            // Create server
            Server<Object> server = new Server<Object>(TEST_HOST, TEST_PORT, 
                    TEST_AUTHENTICATOR, testManager);
            
            // Check server properties
            assertEquals(TEST_HOST, server.getHost());
            assertEquals(TEST_PORT, server.getPort());
            assertEquals(TEST_AUTHENTICATOR, server.getAuthenticator());
            assertEquals(testManager, server.getSessionManager());
            
            // Stop server
            server.stop();
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this, e, "Server lifecycle test failed: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Test session management (login/logout)
     */
    @Test
    public void sessionManagement() throws Exception {
        try {
            // Create client and server
            Client client = new Client(TEST_HOST, TEST_PORT, TEST_APP);
            Server<Object> server = new Server<Object>(TEST_HOST, TEST_PORT,
                    TEST_AUTHENTICATOR, testManager);
            
            // Test not logged in state
            try {
                client.assertValidSession();
                fail("Should fail when not logged in");
            } catch (I18NException ex) {
                assertEquals(Messages.NOT_LOGGED_IN, ex.getI18NBoundMessage());
            }
            
            // Test login
            client.login(TEST_USER, TEST_PASSWORD);
            SessionId id = client.getSessionId();
            assertNotNull("Session ID should be created", id);
            assertEquals("Session ID should be in context", id, client.getContext().getSessionId());
            assertEquals("User should be in session manager", TEST_USER, testManager.get(id).getUser());
            
            // Test already logged in
            try {
                client.login(TEST_USER, TEST_PASSWORD);
                fail("Should fail when already logged in");
            } catch (I18NException ex) {
                assertEquals(Messages.ALREADY_LOGGED_IN, ex.getI18NBoundMessage());
            }
            
            // Test logout
            client.logout();
            assertNull("Session ID should be null after logout", client.getSessionId());
            assertNull("Session ID should be null in context", client.getContext().getSessionId());
            assertNull("Session should be removed from manager", testManager.get(id));
            
            // Test bad credentials
            try {
                client.login(TEST_USER_D, TEST_PASSWORD);
                fail("Should fail with bad credentials");
            } catch (RemoteException ex) {
                assertEquals(Messages.BAD_CREDENTIALS,
                         ((I18NException)ex.getCause()).getI18NBoundMessage());
            }
            
            // Cleanup
            client.logout();
            server.stop();
            
            // Test server stopped
            try {
                client.login(TEST_USER, TEST_PASSWORD);
                fail("Should fail when server is stopped");
            } catch (WebServiceException ex) {
                // Expected
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this, e, "Session management test failed: {}", e.getMessage());
            throw e;
        }
    }
}