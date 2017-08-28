package org.marketcetera.saclient;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.marketcetera.client.ClientManager;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.strategyengine.client.ConnectionException;
import org.marketcetera.strategyengine.client.SEClient;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Tests {@link SEClientFactoryImpl}.
 *
 * @author anshul@marketcetera.com
 * @version $Id: SEClientFactoryTest.java 17242 2016-09-02 16:46:48Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: SEClientFactoryTest.java 17242 2016-09-02 16:46:48Z colin $")
public class SEClientFactoryTest {
    /**
     * Verifies the singleton instance.
     */
    @Test
    public void singleton() {
        assertNotNull(SEClientFactoryImpl.getInstance());
        assertSame(SEClientFactoryImpl.getInstance(), SEClientFactoryImpl.getInstance());
    }

    /**
     * Tests client connection failures and success.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void connect() throws Exception {
        final String creds = MockStrategyEngine.USER_CREDS;
        MockStrategyEngine mockSE = new MockStrategyEngine();
        try {
            MockStrategyEngine.startServerAndClient();
            //null parameters
            new ExpectedFailure<NullPointerException>(){
                @Override
                protected void run() throws Exception {
                    SEClient client = SEClientFactoryImpl.getInstance().create(null);
                    client.start();
                }
            };
            //WS failure
            new ExpectedFailure<ConnectionException>(Messages.ERROR_WS_CONNECT,
                    MockStrategyEngine.WS_HOSTNAME, "9009", creds) {
                @Override
                protected void run() throws Exception {
                    SEClient client = SEClientFactoryImpl.getInstance().create(new SEClientParameters(creds,
                                                                                                      creds,
                                                                                                      MockStrategyEngine.DEFAULT_URL,
                                                                                                      MockStrategyEngine.WS_HOSTNAME,
                                                                                                      9009));
                    client.start();
                }
            };
            //JMS failure
            final String url = "tcp://localhost:61619";
            new ExpectedFailure<ConnectionException>(Messages.ERROR_JMS_CONNECT,
                    url, creds) {
                @Override
                protected void run() throws Exception {
                    SEClient client = SEClientFactoryImpl.getInstance().create(new SEClientParameters(creds,
                            creds, url,
                            MockStrategyEngine.WS_HOSTNAME, MockStrategyEngine.WS_PORT));
                    client.start();
                }
            };
            //Credential failure:  wrong password
            new ExpectedFailure<ConnectionException>(Messages.ERROR_WS_CONNECT,
                    MockStrategyEngine.WS_HOSTNAME,
                    String.valueOf(MockStrategyEngine.WS_PORT), creds) {
                @Override
                protected void run() throws Exception {
                    SEClient client = SEClientFactoryImpl.getInstance().create(new SEClientParameters(creds,
                            "bleh", MockStrategyEngine.DEFAULT_URL,
                            MockStrategyEngine.WS_HOSTNAME, MockStrategyEngine.WS_PORT));
                    client.start();
                }
            };
            //Credential failure:  incorrect username, passes WS but fails at JMS
            final String username = "incorrect";
            new ExpectedFailure<ConnectionException>(Messages.ERROR_JMS_CONNECT,
                    url, username) {
                @Override
                protected void run() throws Exception {
                    SEClient client = SEClientFactoryImpl.getInstance().create(new SEClientParameters(username,
                            username, url,
                            MockStrategyEngine.WS_HOSTNAME, MockStrategyEngine.WS_PORT));
                    client.start();
                }
            };
            assertTrue(ClientManager.getInstance().isCredentialsMatch(creds, creds.toCharArray()));
            //Success
            SEClient client = SEClientFactoryImpl.getInstance().create(new SEClientParameters(creds,
                    creds, MockStrategyEngine.DEFAULT_URL,
                    MockStrategyEngine.WS_HOSTNAME, MockStrategyEngine.WS_PORT));
            client.start();
            client.close();

        } finally {
            MockStrategyEngine.closeServerAndClient();
            mockSE.close();
        }
    }
}
