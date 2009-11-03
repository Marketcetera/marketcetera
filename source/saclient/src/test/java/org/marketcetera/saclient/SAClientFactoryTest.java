package org.marketcetera.saclient;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.client.ClientManager;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/* $License$ */
/**
 * Tests {@link SAClientFactory}.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class SAClientFactoryTest {

    @BeforeClass
    public static void logSetup() {
        LoggerConfiguration.logSetup();
    }

    /**
     * Verifies the singleton instance.
     */
    @Test
    public void singleton() {
        assertNotNull(SAClientFactory.getInstance());
        assertSame(SAClientFactory.getInstance(), SAClientFactory.getInstance());
    }

    /**
     * Tests client connection failures and success.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void connect() throws Exception {
        final String creds = MockStrategyAgent.USER_CREDS;
        MockStrategyAgent mockSA = new MockStrategyAgent();
        try {
            MockStrategyAgent.startServerAndClient();
            //null parameters
            new ExpectedFailure<NullPointerException>(){
                @Override
                protected void run() throws Exception {
                    SAClientFactory.getInstance().create(null);
                }
            };
            //WS failure
            new ExpectedFailure<ConnectionException>(Messages.ERROR_WS_CONNECT,
                    MockStrategyAgent.WS_HOSTNAME, "9009", creds) {
                @Override
                protected void run() throws Exception {
                    SAClientFactory.getInstance().create(new SAClientParameters(creds,
                            creds.toCharArray(), MockStrategyAgent.DEFAULT_URL,
                            MockStrategyAgent.WS_HOSTNAME, 9009));
                }
            };
            //JMS failure
            final String url = "tcp://localhost:61619";
            new ExpectedFailure<ConnectionException>(Messages.ERROR_JMS_CONNECT,
                    url, creds) {
                @Override
                protected void run() throws Exception {
                    SAClientFactory.getInstance().create(new SAClientParameters(creds,
                            creds.toCharArray(), url,
                            MockStrategyAgent.WS_HOSTNAME, MockStrategyAgent.WS_PORT));
                }
            };
            //Credential failure:  wrong password
            new ExpectedFailure<ConnectionException>(Messages.ERROR_WS_CONNECT,
                    MockStrategyAgent.WS_HOSTNAME,
                    String.valueOf(MockStrategyAgent.WS_PORT), creds) {
                @Override
                protected void run() throws Exception {
                    SAClientFactory.getInstance().create(new SAClientParameters(creds,
                            "bleh".toCharArray(), MockStrategyAgent.DEFAULT_URL,
                            MockStrategyAgent.WS_HOSTNAME, MockStrategyAgent.WS_PORT));
                }
            };
            //Credential failure:  incorrect username, passes WS but fails at JMS
            final String username = "incorrect";
            new ExpectedFailure<ConnectionException>(Messages.ERROR_JMS_CONNECT,
                    url, username) {
                @Override
                protected void run() throws Exception {
                    SAClientFactory.getInstance().create(new SAClientParameters(username,
                            username.toCharArray(), url,
                            MockStrategyAgent.WS_HOSTNAME, MockStrategyAgent.WS_PORT));
                }
            };
            assertTrue(ClientManager.getInstance().isCredentialsMatch(creds, creds.toCharArray()));
            //Success
            SAClientFactory.getInstance().create(new SAClientParameters(creds,
                    creds.toCharArray(), MockStrategyAgent.DEFAULT_URL,
                    MockStrategyAgent.WS_HOSTNAME, MockStrategyAgent.WS_PORT))
                    .close();

        } finally {
            MockStrategyAgent.closeServerAndClient();
            mockSA.close();
        }
    }
}
