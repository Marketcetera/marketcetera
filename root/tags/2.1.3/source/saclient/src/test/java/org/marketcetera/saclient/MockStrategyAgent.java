package org.marketcetera.saclient;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.stateful.Server;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.stateless.ServiceInterface;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.MockConfigProvider;
import org.marketcetera.modules.remote.receiver.ReceiverFactory;
import org.marketcetera.modules.remote.receiver.ClientLoginModule;
import org.marketcetera.client.MockServer;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ClientParameters;
import org.marketcetera.client.MockLoginModule;
import org.apache.commons.lang.ObjectUtils;

import javax.security.auth.login.Configuration;
import javax.security.auth.login.AppConfigurationEntry;
import java.util.Collections;
import java.util.HashMap;

/* $License$ */
/**
 * A mock server that simulates the remoting interfaces of a strategy agent
 * for testing the SA client.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
class MockStrategyAgent {
    /**
     * Starts the mock strategy agent.
     *
     * @throws Exception if there were unexpected failures.
     */
    MockStrategyAgent() throws Exception {
        mManager = new ModuleManager();
        MockConfigProvider provider = new MockConfigProvider();
        provider.addDefault(ReceiverFactory.INSTANCE_URN, "URL", DEFAULT_URL);
        provider.addDefault(ReceiverFactory.INSTANCE_URN, "SkipJAASConfiguration", String.valueOf(true));
        mManager.setConfigurationProvider(provider);
        mManager.init();
        //Initialize Web services
        SessionManager<Object> sessionManager=
            new SessionManager<Object>
            (new MockSessionFactory(), SessionManager.INFINITE_SESSION_LIFESPAN);
        mServer=new Server<Object>(WS_HOSTNAME, WS_PORT,
                new Authenticator(){
                 @Override
                 public boolean shouldAllow(StatelessClientContext context,
                                            String user,
                                            char[] password) throws I18NException {
                     return ObjectUtils.equals(user,String.valueOf(password));
                 }
             },sessionManager);
        mService = new MockSAServiceImpl(sessionManager);
        mRemoteService = mServer.publish(mService, SAService.class);
    }

    /**
     * Returns the module manager used by the mock strategy agent.
     *
     * @return the module manager.
     */
    ModuleManager getManager() {
        return mManager;
    }

    /**
     * Stops the mock strategy agent.
     *
     * @throws Exception if there were unexpected failures.
     */
    void close() throws Exception {
        mManager.stop();
        if(mRemoteService != null) {
            mRemoteService.stop();
        }
        mServer.stop();
    }

    /**
     * Returns the mock service implementation.
     *
     * @return the mock service implementation.
     */
    MockSAServiceImpl getService() {
        return mService;
    }

    /**
     * Starts the Mock Server and initializes a client connection to it.
     *
     * @throws Exception if there were unexpected failures.
     */
    static void startServerAndClient() throws Exception {
        setupConfiguration();
        //Initialize Mock Server and client
        sMockServer = new MockServer();
        ClientManager.init(new ClientParameters(USER_CREDS, USER_CREDS.toCharArray(),
                MockServer.URL, Server.DEFAULT_HOST, Server.DEFAULT_PORT));
    }

    /**
     * Closes the client connection if it's initialized. And stops the
     * Mock Server if it was started.
     *
     * @throws Exception if there were unexpected failures.
     */
    static void closeServerAndClient() throws Exception {
        if (ClientManager.isInitialized()) {
            ClientManager.getInstance().close();
        }
        if (sMockServer != null) {
            sMockServer.close();
            sMockServer = null;
        }
    }

    /**
     * Creates a client connection to this Mock Strategy Agent.
     *
     * @return the client.
     *
     * @throws ConnectionException if there were errors connecting.
     */
    static SAClient connectTo() throws ConnectionException {
        return SAClientFactory.getInstance().create(DEFAULT_PARAMETERS);
    }

    /**
     * Sets up the JAAS Configuration such that both Client's test Mock server
     * and remote-receiver's can work.
     */
    private static void setupConfiguration() {
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

    public static final String WS_HOSTNAME = "localhost";
    public static final int WS_PORT = 9001;
    public static final String DEFAULT_URL = "tcp://localhost:61617";
    static final String USER_CREDS = "blue";
    static final SAClientParameters DEFAULT_PARAMETERS = new SAClientParameters(USER_CREDS,
            USER_CREDS.toCharArray(), DEFAULT_URL, WS_HOSTNAME, WS_PORT);
    private volatile static MockServer sMockServer;
    private volatile Server<Object> mServer;
    private volatile ModuleManager mManager;
    private volatile ServiceInterface mRemoteService;
    private final MockSAServiceImpl mService;
}
