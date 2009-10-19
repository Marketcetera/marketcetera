package org.marketcetera.saclient;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.core.Util;
import org.marketcetera.core.LoggerConfiguration;
import org.junit.*;
import static org.junit.Assert.*;
import org.hamcrest.Matchers;

import java.util.Arrays;

/* $License$ */
/**
 * Tests aspects of {@link SAClient} related to it's ability to connect
 * with the remote strategy agent.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class SAClientConnectionTest {
    /**
     * verifies behavior of Web services after the connection to the client
     * has been closed.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void closedFailures() throws Exception {
        final SAClient saclient = MockStrategyAgent.connectTo();
        //Close the connection
        saclient.close();
        //verify that the saclient fails all API invocations
        new ClosedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.createStrategy(null);
            }
        };
        new ClosedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.delete(null);
            }
        };
        new ClosedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.getInstances(null);
            }
        };
        new ClosedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.getModuleInfo(null);
            }
        };
        new ClosedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.getProperties(null);
            }
        };
        new ClosedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.getProviders();
            }
        };
        new ClosedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.getStrategyCreateParms(null);
            }
        };
        new ClosedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.setProperties(null, null);
            }
        };
        new ClosedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.start(null);
            }
        };
        new ClosedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.stop(null);
            }
        };
        final SAClientTestBase.MyConnectionStatusListener listener =
                new SAClientTestBase.MyConnectionStatusListener();
        new ClosedFailure() {
            @Override
            protected void run() throws Exception {
                saclient.addConnectionStatusListener(listener);
            }
        };
        new ClosedFailure(){
            @Override
            protected void run() throws Exception {
                saclient.removeConnectionStatusListener(listener);
            }
        };
        final SAClientTestBase.MyDataReceiver receiver = new SAClientTestBase.MyDataReceiver();
        new ClosedFailure(){
            @Override
            protected void run() throws Exception {
                saclient.addDataReceiver(receiver);
            }
        };
        new ClosedFailure(){
            @Override
            protected void run() throws Exception {
                saclient.removeDataReciever(receiver);
            }
        };
        //These methods can be invoked even when the client is not connected.
        verifyParameters(MockStrategyAgent.DEFAULT_PARAMETERS,
                saclient.getParameters());
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
        final SAClient saclient = MockStrategyAgent.connectTo();
        SAClientTestBase.MyConnectionStatusListener listener =
                new SAClientTestBase.MyConnectionStatusListener();
        saclient.addConnectionStatusListener(listener);
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
        SAClientTestBase.MyDataReceiver receiver = new SAClientTestBase.MyDataReceiver();
        saclient.addDataReceiver(receiver);
        saclient.removeDataReciever(receiver);
        verifyParameters(MockStrategyAgent.DEFAULT_PARAMETERS, saclient.getParameters());
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
        final SAClient saclient = MockStrategyAgent.connectTo();
        new ExpectedFailure<NullPointerException>(null){
            @Override
            protected void run() throws Exception {
                saclient.addConnectionStatusListener(null);
            }
        };
        new ExpectedFailure<NullPointerException>(null){
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
        SAClient client = MockStrategyAgent.connectTo();
        SAClientTestBase.MyConnectionStatusListener listener =
                new SAClientTestBase.MyConnectionStatusListener();
        client.addConnectionStatusListener(listener);
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
        client = MockStrategyAgent.connectTo();
        client.addConnectionStatusListener(listener);
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
        SAClient client = MockStrategyAgent.connectTo();
        final SAClientTestBase.MyConnectionStatusListener listener1 =
                new SAClientTestBase.MyConnectionStatusListener();
        SAClientTestBase.MyConnectionStatusListener listener2 =
                new SAClientTestBase.MyConnectionStatusListener();
        //configure both listeners to fail
        listener1.setFail(true);
        listener2.setFail(true);
        client.addConnectionStatusListener(listener1);
        client.addConnectionStatusListener(listener2);
        assertFalse(listener1.hasData());
        assertFalse(listener2.hasData());
        //close the connection
        client.close();
        //See if we get a notification
        assertFalse(listener1.getNext());
        assertFalse(listener2.getNext());
        //verify that listener throws exception when set to fail
        new ExpectedFailure<IllegalStateException>(null){
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
        SAClient client = MockStrategyAgent.connectTo();
        final SAClientTestBase.MyConnectionStatusListener listener =
                new SAClientTestBase.MyConnectionStatusListener();
        client.addConnectionStatusListener(listener);
        //Add listener twice
        client.addConnectionStatusListener(listener);
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
        SAClient client = MockStrategyAgent.connectTo();
        final SAClientTestBase.MyConnectionStatusListener listener1 =
                new SAClientTestBase.MyConnectionStatusListener();
        SAClientTestBase.MyConnectionStatusListener listener2 =
                new SAClientTestBase.MyConnectionStatusListener();
        client.addConnectionStatusListener(listener1);
        client.addConnectionStatusListener(listener2);
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
        SAClient client = MockStrategyAgent.connectTo();
        final SAClientTestBase.MyConnectionStatusListener listener1 =
                new SAClientTestBase.MyConnectionStatusListener();
        SAClientTestBase.MyConnectionStatusListener listener2 =
                new SAClientTestBase.MyConnectionStatusListener();
        client.addConnectionStatusListener(listener1);
        client.addConnectionStatusListener(listener2);
        assertFalse(listener1.hasData());
        assertFalse(listener2.hasData());
        //Remove listener2
        client.removeConnectionStatusListener(listener2);
        //remove a listener that is not added for kicks
        client.removeConnectionStatusListener(new SAClientTestBase.MyConnectionStatusListener());
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
        SAClient client = MockStrategyAgent.connectTo();
        final SAClientTestBase.MyConnectionStatusListener listener0 =
                new SAClientTestBase.MyConnectionStatusListener();
        final SAClientTestBase.MyConnectionStatusListener listener1 =
                new SAClientTestBase.MyConnectionStatusListener();
        SAClientTestBase.MyConnectionStatusListener listener2 =
                new SAClientTestBase.MyConnectionStatusListener();
        client.addConnectionStatusListener(listener0);
        client.addConnectionStatusListener(listener1);
        client.addConnectionStatusListener(listener2);
        //add listener1 twice
        client.addConnectionStatusListener(listener1);
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
                Util.getVersion(SAClientVersion.APP_ID));
    }

    @Before
    public void startAgent() throws Exception {
        mAgent = new MockStrategyAgent();
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
        LoggerConfiguration.logSetup();
        MockStrategyAgent.startServerAndClient();
    }

    @AfterClass
    public static void teardown() throws Exception {
        MockStrategyAgent.closeServerAndClient();
    }
    
    private void verifyParameters(SAClientParameters inExpected,
                                  SAClientParameters inActual) {
        assertEquals(inExpected.getURL(), inActual.getURL());
        assertEquals(inExpected.getHostname(), inActual.getHostname());
        assertEquals(inExpected.getPort(), inActual.getPort());
        assertEquals(inExpected.getUsername(), inActual.getUsername());
        //password should be smudged!
        assertFalse(String.valueOf(inActual.getPassword()),
                Arrays.equals(inExpected.getPassword(), inActual.getPassword()));
    }

    /**
     * Closure for testing saclient failures after it has been closed.
     */
    private static abstract class ClosedFailure
            extends ExpectedFailure<IllegalStateException> {

        protected ClosedFailure() throws Exception {
            super(Messages.CLIENT_CLOSED.getText(), true);
        }
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

    private volatile MockStrategyAgent mAgent;
}
