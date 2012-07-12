package org.marketcetera.strategyagent;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.Node;
import org.marketcetera.util.test.LogTestAssist;
import org.marketcetera.client.MockServer;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ClientParameters;
import org.marketcetera.client.MockLoginModule;
import org.marketcetera.saclient.*;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.modules.remote.receiver.ClientLoginModule;
import org.marketcetera.modules.remote.receiver.ReceiverModule;
import org.junit.*;
import org.apache.log4j.Level;

import javax.security.auth.login.Configuration;
import javax.security.auth.login.AppConfigurationEntry;
import java.util.Collections;
import java.util.HashMap;
import java.net.Socket;
import java.net.ConnectException;

/* $License$ */
/**
 * Tests configuration of remoting capabilities on strategy agent.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class StrategyAgentRemotingConfigTest extends StrategyAgentTestBase {

    /**
     * Initializes the mock server and the client connection so that
     * remote receiver is able to authenticate its clients successfully.
     *
     * @throws Exception if there were unexpected failures.
     */
    @BeforeClass
    public static void createServerAndClient() throws Exception {
        setupConfiguration();
        sServer = new MockServer();
        ClientManager.init(new ClientParameters(DEFAULT_CREDENTIAL,
                DEFAULT_CREDENTIAL.toCharArray(), MockServer.URL,
                Node.DEFAULT_HOST, Node.DEFAULT_PORT));
    }

    /**
     * Stops the mock server and client.
     *
     * @throws Exception if there were unexpected errors.
     */
    @AfterClass
    public static void stopServerAndClient() throws Exception {
        if(ClientManager.isInitialized()) {
            ClientManager.getInstance().close();
        }
        if (sServer != null) {
            sServer.close();
        }
    }

    /**
     * Reset the system properties and the log appender.
     */
    @Before
    public void reset() {
        //unset the system property
        System.clearProperty(WS_HOST_PROPERTY);
        System.clearProperty(RECV_URL_PROPERTY);
        mLogAssist.resetAppender();
    }

    /**
     * Verifies that remote WS is not configured when the ws host property
     * is not set.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void remoteWSUnavailable() throws Exception {
        //set the ws host system property to override the
        //default value.
        System.setProperty(WS_HOST_PROPERTY," ");
        //start agent
        run(createAgent(false));
        //verify that the Web service is available.
        tryConnectTo(WS_HOST, WS_PORT, false);
        //verify that the client cannot connect.
        new ExpectedFailure<ConnectionException>(
                org.marketcetera.saclient.Messages.ERROR_WS_CONNECT){
            @Override
            protected void run() throws Exception {
                SAClientFactory.getInstance().create(
                        new SAClientParameters(DEFAULT_CREDENTIAL,
                                DEFAULT_CREDENTIAL.toCharArray(),
                                RECEIVER_URL, WS_HOST, WS_PORT));
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
    public void remoteJMSUnavailable() throws Exception {
        //set the ws host system property to override the
        //default value.
        System.setProperty(RECV_URL_PROPERTY," ");
        //start agent
        run(createAgent(false));
        //verify that the JMS service is not available.
        tryConnectTo(WS_HOST, JMS_PORT, false);
        //verify that the client cannot connect
        new ExpectedFailure<ConnectionException>(
                org.marketcetera.saclient.Messages.ERROR_JMS_CONNECT){
            @Override
            protected void run() throws Exception {
                SAClientFactory.getInstance().create(
                        new SAClientParameters(DEFAULT_CREDENTIAL,
                                DEFAULT_CREDENTIAL.toCharArray(),
                                RECEIVER_URL, WS_HOST, WS_PORT));
            }
        };
    }

    /**
     * Tests that remote web services are available by default.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void remoteWSAvailable() throws Exception {
        //start agent
        run(createAgent(false));
        //verify that the Web service is available.
        tryConnectTo(WS_HOST, WS_PORT, true);
        mLogAssist.assertSomeEvent(Level.INFO, TestAgent.class.getName(),
                Messages.LOG_REMOTE_WS_CONFIGURED.getText("localhost",
                        String.valueOf(9001)), null);
    }

    /**
     * Tests that remote JMS service becomes available when it is configured.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void remoteJMSAvailable() throws Exception {
        //start agent
        run(createAgent(false));
        //verify that the JMS service is available.
        tryConnectTo(WS_HOST, JMS_PORT, true);
        mLogAssist.assertSomeEvent(Level.INFO, ReceiverModule.class.getName(),
                org.marketcetera.modules.remote.receiver.Messages.RECIEVER_REMOTING_CONFIGURED.getText(RECEIVER_URL),
                null);
    }

    /**
     * Creates an instance and configures the log tester with the log
     * categories that it needs to track.
     */
    public StrategyAgentRemotingConfigTest() {
        mLogAssist = new LogTestAssist(TestAgent.class.getName(), Level.INFO);
        mLogAssist.trackLogger(ReceiverModule.class.getName(), Level.INFO);
    }

    /**
     * Sets up the JAAS Configuration such that both Client's test Mock server
     * and remote-receiver's can work.
     */
    static void setupConfiguration() {
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
            new Socket(inHost, inPort).close();
        } else {
            new ExpectedFailure<ConnectException>(){
                @Override
                protected void run() throws Exception {
                    new Socket(inHost, inPort).close();
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
    private final LogTestAssist mLogAssist;
    private static final String WS_HOST_PROPERTY = "metc.sa.ws.host";
    private static final String RECV_URL_PROPERTY = "metc.sa.recv.url";
}
