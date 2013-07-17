package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.Node;
import org.marketcetera.module.*;
import org.junit.*;


/* $License$ */
/**
 * Tests the {@link ClientModule} module when the client is initialized
 * before the module is created as it would be within photon.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ModulePreConfiguredTest extends ClientModuleTestBase {

    @Before
    public void clientSetup() throws Exception {
        mManager = new ModuleManager();
        //Initialize the client before initializing the module manager
        String username = "me";
        ClientParameters parameters = new ClientParameters(username,
                username.toCharArray(), MockServer.URL,
                Node.DEFAULT_HOST, Node.DEFAULT_PORT, IDPREFIX);
        ClientManager.init(parameters);
        mManager.init();
    }
}