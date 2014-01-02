package org.marketcetera.strategyagent;

import java.net.ConnectException;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

import org.junit.*;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ClientParameters;
import org.marketcetera.client.MockLoginModule;
import org.marketcetera.client.MockServer;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.modules.remote.receiver.ClientLoginModule;
import org.marketcetera.saclient.ConnectionException;
import org.marketcetera.saclient.SAClientFactory;
import org.marketcetera.saclient.SAClientParameters;
import org.marketcetera.util.ws.stateless.Node;

/* $License$ */
/**
 * Tests configuration of remoting capabilities on strategy agent.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
public class StrategyAgentRemotingConfigTest
        extends StrategyAgentTestBase
{
    /**
     * Initializes the mock server and the client connection so that
     * remote receiver is able to authenticate its clients successfully.
     *
     * @throws Exception if there were unexpected failures.
     */
    @BeforeClass
    public static void createServerAndClient()
            throws Exception
    {
        setupConfiguration();
        sServer = new MockServer();
        ClientManager.init(new ClientParameters(DEFAULT_CREDENTIAL,
                                                DEFAULT_CREDENTIAL.toCharArray(),
                                                MockServer.URL,
                                                Node.DEFAULT_HOST,
                                                Node.DEFAULT_PORT));
    }
    /**
     * Stops the mock server and client.
     *
     * @throws Exception if there were unexpected errors.
     */
    @AfterClass
    public static void stopServerAndClient()
            throws Exception
    {
        if(ClientManager.isInitialized()) {
            ClientManager.getInstance().close();
        }
        if(sServer != null) {
            sServer.close();
        }
    }
    /**
     * Reset the system properties and the log appender.
     */
    @Before
    public void reset()
    {
        useWs = true;
    }
    /**
     * Runs after each test.
     *
     * @throws Exception if there were unexpected errors.
     */
    @After
    public void cleanup()
            throws Exception
    {
        shutdownSa();
    }
    /**
     * Verifies that remote WS is not configured when the ws host property
     * is not set.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void remoteWSUnavailable()
            throws Exception
    {
        // set the ws port to override the default value.
        wsPort = 9002;
        // start agent (at port 9002)
        createSaWith();
        // verify that the Web service is available (it's not, because we're looking at port 9001)
        tryConnectTo(WS_HOST,
                     WS_PORT,
                     false);
        // verify that the client cannot connect
        new ExpectedFailure<ConnectionException>(org.marketcetera.saclient.Messages.ERROR_WS_CONNECT) {
            @Override
            protected void run()
                    throws Exception
            {
                SAClientFactory.getInstance().create(new SAClientParameters(DEFAULT_CREDENTIAL,
                                                                            DEFAULT_CREDENTIAL.toCharArray(),
                                                                            RECEIVER_URL,
                                                                            WS_HOST,
                                                                            WS_PORT));
            }
        };
    }
    /**
     * Tests that remote JMS service is unavailable when it's not
     * configured.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void remoteJMSUnavailable()
            throws Exception
    {
        // set the ws host system property to override the default value.
        jmsPort = 61618;
        buildJmsUrl();
        //start agent
        createSaWith();
        //verify that the JMS service is not available.
        tryConnectTo(WS_HOST,
                     JMS_PORT,
                     false);
        //verify that the client cannot connect
        new ExpectedFailure<ConnectionException>(org.marketcetera.saclient.Messages.ERROR_JMS_CONNECT) {
            @Override
            protected void run()
                    throws Exception
            {
                SAClientFactory.getInstance().create(new SAClientParameters(DEFAULT_CREDENTIAL,
                                                                            DEFAULT_CREDENTIAL.toCharArray(),
                                                                            RECEIVER_URL,
                                                                            WS_HOST,
                                                                            WS_PORT));
            }
        };
    }
    /**
     * Tests that remote web services are available by default.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void remoteWSAvailable()
            throws Exception
    {
        //start agent
        createSaWith();
        //verify that the Web service is available.
        tryConnectTo(WS_HOST, WS_PORT, true);
    }
    /**
     * Tests that remote JMS service becomes available when it is configured.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void remoteJMSAvailable()
            throws Exception
    {
        // start agent
        createSaWith();
        // verify that the JMS service is available
        tryConnectTo(WS_HOST,
                     JMS_PORT,
                     true);
    }
    /**
     * Sets up the JAAS Configuration such that both Client's test Mock server
     * and remote-receiver's can work.
     */
    static void setupConfiguration()
    {
        Configuration.setConfiguration(new Configuration() {
            public AppConfigurationEntry[] getAppConfigurationEntry(String inName) {
                if("remoting-amq-domain".equals(inName)) {
                    //the login module for the receiver module.
                    return new AppConfigurationEntry[]{
                            new AppConfigurationEntry(ClientLoginModule.class.getName(),
                                    AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                                    Collections.unmodifiableMap(new HashMap<String, String>()))
                    };
                } else if ("test-amq-domain".equals(inName)) {
                    //the login module for mock server
                    return new AppConfigurationEntry[]{
                            new AppConfigurationEntry(MockLoginModule.class.getName(),
                                    AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                                    Collections.unmodifiableMap(new HashMap<String, String>()))
                    };
                }
                return null;
            }
        });
    }
    /**
     * Tries connecting to the specified host and port and verifies if the
     * connection is successful or not.
     *
     * @param inHost the hostname.
     * @param inPort the port number.
     * @param inSuccess if the connection should be successful.
     *
     * @throws Exception if there were unexpected failures.
     */
    private static void tryConnectTo(final String inHost,
                                     final int inPort,
                                     boolean inSuccess) throws Exception {
        if(inSuccess) {
            new Socket(inHost,
                       inPort).close();
        } else {
            new ExpectedFailure<ConnectException>() {
                @Override
                protected void run()
                        throws Exception
                {
                    new Socket(inHost,
                               inPort).close();
                }
            };
        }
    }
    private static MockServer sServer;
    private static final String DEFAULT_CREDENTIAL = "DrNo";
    private static final String WS_HOST = "localhost";
    private static final int WS_PORT = 9001;
    private static final int JMS_PORT = 61617;
    private static final String RECEIVER_URL = "tcp://" + WS_HOST + ":" + JMS_PORT;
}
