package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.Node;
import org.marketcetera.module.*;
import org.marketcetera.core.LoggerConfiguration;
import org.junit.Test;
import org.junit.BeforeClass;

import javax.management.JMX;

/* $License$ */
/**
 * ModuleFailuresTest
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ModuleFailuresTest extends ModuleTestBase {
    @BeforeClass
    public static void setup() {
        LoggerConfiguration.logSetup();
    }
    @Test
    public void createModuleFailures() throws Exception {
        MockConfigurationProvider provider = new MockConfigurationProvider();
        provider.setURL(MockServer.URL);
        final ModuleManager m1 = new ModuleManager();
        m1.setConfigurationProvider(provider);
        new ExpectedFailure<ModuleCreationException>(Messages.CREATE_MODULE_ERROR){
            protected void run() throws Exception {
                m1.init();
            }
        };
        m1.stop();
    }
    @Test
    public void moduleOperationFailures() throws Exception {
        String username = "u";
        //Initialize the server and client
        MockServer mockServer = null;
        try {
            mockServer = new MockServer();
            ClientManager.init(new ClientParameters(username, username.toCharArray(),
                    MockServer.URL, Node.DEFAULT_HOST, Node.DEFAULT_PORT));
            ModuleManager manager = new ModuleManager();
            manager.init();
            assertModuleInfo(manager, ClientModuleFactory.INSTANCE_URN,
                    ModuleState.STARTED, null, null, false, true, true,
                    true, false);
            final ClientModuleMXBean instance = JMX.newMXBeanProxy(getMBeanServer(),
                    ClientModuleFactory.INSTANCE_URN.toObjectName(),
                    ClientModuleMXBean.class);
            //Now close the client
            ClientManager.getInstance().close();
            //Verify failures.
            new ExpectedFailure<RuntimeException>(Messages.
                    CLIENT_NOT_INITIALIZED.getText()){
                protected void run() throws Exception {
                    instance.getLastConnectTime();
                }
            };
            new ExpectedFailure<RuntimeException>(Messages.
                    CLIENT_NOT_INITIALIZED.getText()){
                protected void run() throws Exception {
                    instance.getParameters();
                }
            };
            new ExpectedFailure<RuntimeException>(Messages.
                    CLIENT_NOT_INITIALIZED.getText()){
                protected void run() throws Exception {
                    instance.reconnect();
                }
            };
            manager.stop();
        } finally {
            if(mockServer != null) {
                mockServer.close();
            }
        }
    }
}
