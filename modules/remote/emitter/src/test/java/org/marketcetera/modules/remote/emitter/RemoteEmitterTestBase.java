package org.marketcetera.modules.remote.emitter;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.Node;
import org.marketcetera.module.ModuleTestBase;
import org.marketcetera.module.MockConfigProvider;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.client.MockServer;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ClientParameters;
import org.marketcetera.client.MockLoginModule;
import org.marketcetera.modules.remote.receiver.ReceiverFactory;
import org.marketcetera.modules.remote.receiver.ClientLoginModule;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.AfterClass;

import javax.security.auth.login.Configuration;
import javax.security.auth.login.AppConfigurationEntry;
import java.util.Collections;
import java.util.HashMap;

/* $License$ */
/**
 * A base class the collects common code between various emitter unit tests.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class RemoteEmitterTestBase extends ModuleTestBase {
    /**
     * Stops the module manager and the mock server.
     *
     * @throws Exception if there were errors
     */
    @After
    public void stopManager() throws Exception {
        if (mManager != null) {
            mManager.stop();
            mManager = null;
        }
    }

    /**
     * Sets up the mock server and connects the client to it so that
     * receiver module's authentication succeeds.
     *
     * @throws Exception if there were errors.
     */
    @BeforeClass
    public static void setupClientAndServer() throws Exception {
        //Do JAAS configuration so that both mock server and remote receiver
        //can work.
        setupConfiguration();
        //Create a MockServer first to ensure that client auth succeeds
        sServer = new MockServer();
        //Initialize the client connection.
        ClientManager.init(new ClientParameters(DEFAULT_CREDENTIAL,
                DEFAULT_CREDENTIAL.toCharArray(), MockServer.URL,
                Node.DEFAULT_HOST, Node.DEFAULT_PORT));
    }

    /**
     * Closes the client connection & shuts down the mock server.
     *
     * @throws Exception if there were errors
     */
    @AfterClass
    public static void shutdownClientAndServer() throws Exception {
        if (ClientManager.isInitialized()) {
            ClientManager.getInstance().close();
        }
        if(sServer != null) {
            sServer.close();
            sServer = null;
        }
    }

    /**
     * Initialize the manager with the default URL and credentials.
     *
     * @throws Exception if there were errors.
     */
    protected void initManager() throws Exception {
        initManager(configProviderWithURLValue(DEFAULT_URL));
    }

    /**
     * Creates and configures a mock configuration provider with the
     * supplied URL and default credentials.
     *
     * @param inUrl the URL for the receiver module.
     *
     * @return the configured mock configuration provider.
     */
    protected MockConfigProvider configProviderWithURLValue(String inUrl) {
        MockConfigProvider prov = new MockConfigProvider();
        prov.addDefault(ReceiverFactory.INSTANCE_URN, "URL", inUrl);
        return prov;
    }

    /**
     * Initialize the module manager with the configuration provided with
     * the supplied configuration provider.
     *
     * @param inProvider the configured configuration provider.
     *
     * @throws Exception if there were errors.
     */
    private void initManager(MockConfigProvider inProvider) throws Exception {
        mManager = new ModuleManager();
        mManager.setConfigurationProvider(inProvider);
        mManager.init();
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

    protected ModuleManager mManager;
    protected static final String DEFAULT_CREDENTIAL = "why";
    protected static final String DEFAULT_URL = "tcp://localhost:61617";
    private static MockServer sServer;
}
