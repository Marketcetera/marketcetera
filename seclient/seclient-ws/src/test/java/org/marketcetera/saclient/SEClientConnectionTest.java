package org.marketcetera.saclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.Util;
import org.marketcetera.core.VersionInfo;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.strategyengine.client.ConnectionException;
import org.marketcetera.strategyengine.client.SEClient;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Tests aspects of {@link SEClient} related to it's ability to connect
 * with the remote strategy agent.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class SEClientConnectionTest {
    /**
     * verifies behavior of Web services after the connection to the client
     * has been closed.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void closedFailures() throws Exception {
        final SEClient saclient = MockStrategyEngine.connectTo();
        //Close the connection
        saclient.close();
        //verify that the saclient fails all API invocations
        new DisconnectedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.createStrategy(null);
            }
        };
        new DisconnectedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.delete(null);
            }
        };
        new DisconnectedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.getInstances(null);
            }
        };
        new DisconnectedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.getModuleInfo(null);
            }
        };
        new DisconnectedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.getProperties(null);
            }
        };
        new DisconnectedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.getProviders();
            }
        };
        new DisconnectedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.getStrategyCreateParms(null);
            }
        };
        new DisconnectedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.setProperties(null, null);
            }
        };
        new DisconnectedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.start(null);
            }
        };
        new DisconnectedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.stop(null);
            }
        };
        //These methods can be invoked even when the client is not connected.
        SEClientTestBase.MyConnectionStatusListener listener = new SEClientTestBase.MyConnectionStatusListener();
        saclient.addConnectionStatusListener(listener);
        saclient.removeConnectionStatusListener(listener);
        SEClientTestBase.MyDataReceiver receiver = new SEClientTestBase.MyDataReceiver();
        saclient.addDataReceiver(receiver);
        saclient.removeDataReciever(receiver);
        //We can close it again if we want
        saclient.close();
    }

    /**
     * Verifies the behavior of services after the client gets disconnected.
     *
     * @throws Exception if there unexpected errors.
     */
    @Test
    public void disconnectedFailures() throws Exception {
        final SEClient saclient = MockStrategyEngine.connectTo();
        SEClientTestBase.MyConnectionStatusListener listener =
                new SEClientTestBase.MyConnectionStatusListener();
        saclient.addConnectionStatusListener(listener);
        listener.reset();
        //stop the agent to force disconnection
        stopAgent();
        //wait until the notification has been processed
        assertFalse(listener.getNext());
        //verify that the saclient fails all API invocations
        new DisconnectedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.createStrategy(null);
            }
        };
        new DisconnectedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.delete(null);
            }
        };
        new DisconnectedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.getInstances(null);
            }
        };
        new DisconnectedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.getModuleInfo(null);
            }
        };
        new DisconnectedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.getProperties(null);
            }
        };
        new DisconnectedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.getProviders();
            }
        };
        new DisconnectedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.getStrategyCreateParms(null);
            }
        };
        new DisconnectedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.setProperties(null, null);
            }
        };
        new DisconnectedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.start(null);
            }
        };
        new DisconnectedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.stop(null);
            }
        };
        //These methods can be invoked even when the client is not connected.
        saclient.addConnectionStatusListener(listener);
        saclient.removeConnectionStatusListener(listener);
        SEClientTestBase.MyDataReceiver receiver = new SEClientTestBase.MyDataReceiver();
        saclient.addDataReceiver(receiver);
        saclient.removeDataReciever(receiver);
        //We can close it again if we want
        saclient.close();
    }

    /**
     * Tests the behavior when null listeners are added / removed.
     *
     * @throws Exception if there were unexpected exceptions.
     */
    @Test
    public void nullListeners() throws Exception {
        final SEClient saclient = MockStrategyEngine.connectTo();
        new ExpectedFailure<NullPointerException>(){
            @Override
            protected void run() throws Exception {
                saclient.addConnectionStatusListener(null);
            }
        };
        new ExpectedFailure<NullPointerException>(){
            @Override
            protected void run() throws Exception {
                saclient.removeConnectionStatusListener(null);
            }
        };
        saclient.close();
    }

    /**
     * Verifies that connection notifications are delivered when
     * the connection fails or is closed.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test(timeout = 100000)
    public void connectionNotifications() throws Exception {
        SEClient client = MockStrategyEngine.connectTo();
        SEClientTestBase.MyConnectionStatusListener listener = new SEClientTestBase.MyConnectionStatusListener();
        client.addConnectionStatusListener(listener);
        listener.reset();
        assertFalse(listener.hasData());
        //close the connection
        client.close();
        //See if we get a notification
        assertFalse(listener.getNext());
        //Wait a little more
        Thread.sleep(1000);
        //There should be nothing more
        assertFalse(listener.hasData());

        //Connect again
        client = MockStrategyEngine.connectTo();
        client.addConnectionStatusListener(listener);
        listener.reset();
        //but this time kill the server
        stopAgent();
        //See if we get a notification
        assertFalse(listener.getNext());
        //verify that all the WS invocations fail
        //Wait a little more
        Thread.sleep(1000);
        //There should be nothing more
        assertFalse(listener.hasData());
        //Close the client
        client.close();
    }

    /**
     * Verify that if a listener throws an exception, it doesn't impact
     * delivery of notifications to other registered listeners.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test(timeout = 100000)
    public void connectionListenerFailure() throws Exception {
        SEClient client = MockStrategyEngine.connectTo();
        final SEClientTestBase.MyConnectionStatusListener listener1 =
                new SEClientTestBase.MyConnectionStatusListener();
        SEClientTestBase.MyConnectionStatusListener listener2 =
                new SEClientTestBase.MyConnectionStatusListener();
        //configure both listeners to fail
        client.addConnectionStatusListener(listener1);
        client.addConnectionStatusListener(listener2);
        listener1.reset();
        listener2.reset();
        listener1.setFail(true);
        listener2.setFail(true);
        assertFalse(listener1.hasData());
        assertFalse(listener2.hasData());
        //close the connection
        client.close();
        //See if we get a notification
        assertFalse(listener1.getNext());
        assertFalse(listener2.getNext());
        //verify that listener throws exception when set to fail
        new ExpectedFailure<IllegalStateException>(){
            @Override
            protected void run() throws Exception {
                listener1.receiveConnectionStatus(false);
            }
        };
    }

    /**
     * Tests notifications when connection listener is added twice.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test(timeout = 100000)
    public void listenerDuplicates() throws Exception {
        SEClient client = MockStrategyEngine.connectTo();
        final SEClientTestBase.MyConnectionStatusListener listener =
                new SEClientTestBase.MyConnectionStatusListener();
        client.addConnectionStatusListener(listener);
        //Add listener twice
        client.addConnectionStatusListener(listener);
        listener.reset();
        assertFalse(listener.hasData());
        //close the connection
        client.close();
        //See if we get a notification
        assertFalse(listener.getNext());
        //And that listener got notified twice
        assertFalse(listener.getNext());
    }

    /**
     * Tests that multiple listeners are notified in the reverse order
     * of their addition.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test(timeout = 100000)
    public void listenerOrder() throws Exception {
        SEClient client = MockStrategyEngine.connectTo();
        final SEClientTestBase.MyConnectionStatusListener listener1 =
                new SEClientTestBase.MyConnectionStatusListener();
        SEClientTestBase.MyConnectionStatusListener listener2 =
                new SEClientTestBase.MyConnectionStatusListener();
        client.addConnectionStatusListener(listener1);
        client.addConnectionStatusListener(listener2);
        listener1.reset();
        listener2.reset();
        assertFalse(listener1.hasData());
        assertFalse(listener2.hasData());
        //close the connection
        client.close();
        //See if we get notifications
        assertFalse(listener1.getNext());
        assertFalse(listener2.getNext());
        //And that listener2 is notified before listener1
        assertThat(listener1.getLastAddTime(), Matchers.greaterThan(listener2.getLastAddTime()));
    }

    /**
     * Verifies that removed listeners do not receive notifications.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test(timeout = 100000)
    public void listenerRemove() throws Exception {
        SEClient client = MockStrategyEngine.connectTo();
        final SEClientTestBase.MyConnectionStatusListener listener1 =
                new SEClientTestBase.MyConnectionStatusListener();
        SEClientTestBase.MyConnectionStatusListener listener2 =
                new SEClientTestBase.MyConnectionStatusListener();
        client.addConnectionStatusListener(listener1);
        client.addConnectionStatusListener(listener2);
        listener1.reset();
        listener2.reset();
        assertFalse(listener1.hasData());
        assertFalse(listener2.hasData());
        //Remove listener2
        client.removeConnectionStatusListener(listener2);
        //remove a listener that is not added for kicks
        client.removeConnectionStatusListener(new SEClientTestBase.MyConnectionStatusListener());
        //close the connection
        client.close();
        //Verify listener1 gets notifications
        assertFalse(listener1.getNext());
        //And that listener2 doesn't. We can be sure about the timing because
        //it was added after listener1 it would have been notified before
        //listener1.
        assertFalse(listener2.hasData());
        assertNull(listener2.getLastAddTime());
    }

    /**
     * Tests that when a listener is added more than once, the most
     * recently added instance is removed first.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test(timeout = 100000)
    public void duplicateListenerRemoveOrder() throws Exception {
        SEClient client = MockStrategyEngine.connectTo();
        final SEClientTestBase.MyConnectionStatusListener listener0 =
                new SEClientTestBase.MyConnectionStatusListener();
        final SEClientTestBase.MyConnectionStatusListener listener1 =
                new SEClientTestBase.MyConnectionStatusListener();
        SEClientTestBase.MyConnectionStatusListener listener2 =
                new SEClientTestBase.MyConnectionStatusListener();
        client.addConnectionStatusListener(listener0);
        client.addConnectionStatusListener(listener1);
        client.addConnectionStatusListener(listener2);
        //add listener1 twice
        client.addConnectionStatusListener(listener1);
        listener0.reset();
        listener1.reset();
        listener2.reset();
        assertFalse(listener0.hasData());
        assertFalse(listener1.hasData());
        assertFalse(listener2.hasData());
        //Remove listener1
        client.removeConnectionStatusListener(listener1);
        //close the connection
        client.close();
        //Verify each listener gets a notifications
        assertFalse(listener0.getNext());
        assertFalse(listener1.getNext());
        assertFalse(listener2.getNext());
        //Since listener0 has been notified that listener1 should have
        //received all its notifications. Verify that it has no more
        //notifications.
        assertFalse(listener1.hasData());
        //And that listener1 gets it after listener2, which proves that
        //the most recently added instance was removed. if the other instance
        //was removed this assert will fail.
        assertThat(listener1.getLastAddTime(),
                Matchers.greaterThan(listener2.getLastAddTime()));
        //And that listener0 was notified in the end
        assertThat(listener0.getLastAddTime(),
                Matchers.greaterThan(listener1.getLastAddTime()));
    }

    /**
     * Verifies that the client appID has the correct version.
     */
    @Test
    public void versionTest() {
        assertEquals(SAClientVersion.APP_ID_VERSION,
                     new VersionInfo(Util.getVersion(SAClientVersion.APP_ID)));
    }

    @Before
    public void startAgent() throws Exception {
        mAgent = new MockStrategyEngine();
    }

    @After
    public void stopAgent() throws Exception {
        if (mAgent != null) {
            mAgent.close();
            mAgent = null;
        }
    }

    @BeforeClass
    public static void setup() throws Exception {
        MockStrategyEngine.startServerAndClient();
    }

    @AfterClass
    public static void teardown() throws Exception {
        MockStrategyEngine.closeServerAndClient();
    }
    /**
     * Closure for testing saclient failures after it has been
     * disconnected.
     */
    private static abstract class DisconnectedFailure
            extends ExpectedFailure<ConnectionException> {

        protected DisconnectedFailure() throws Exception {
            super(Messages.CLIENT_DISCONNECTED);
        }
    }

    private volatile MockStrategyEngine mAgent;
}
