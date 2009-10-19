package org.marketcetera.modules.remote.emitter;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.test.LogTestAssist;
import org.marketcetera.core.Pair;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.CopierModuleFactory;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.trade.*;
import org.marketcetera.client.ClientTest;
import org.marketcetera.modules.remote.receiver.ReceiverFactory;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.BeanCreationException;
import org.apache.log4j.Level;

import javax.jms.JMSException;
import javax.security.auth.login.LoginException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.ConnectException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.NotSerializableException;

/* $License$ */
/**
 * Tests {@link RemoteDataEmitter}.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class RemoteDataEmitterTest extends RemoteEmitterTestBase {
    /**
     * Tests for null URL and adapter values.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void nulls() throws Exception {
        //null URL
        new ExpectedFailure<NullPointerException>(null){
            @Override
            protected void run() throws Exception {
                new RemoteDataEmitter(null, "bla", "bla", new MyAdapter());
            }
        };
        //null adapter
        new ExpectedFailure<NullPointerException>(null){
            @Override
            protected void run() throws Exception {
                new RemoteDataEmitter(DEFAULT_URL, "bla", "bla", null);
            }
        };
        //null user name
        new ExpectedFailure<NullPointerException>(null){
            @Override
            protected void run() throws Exception {
                new RemoteDataEmitter(DEFAULT_URL, null, "bla", new MyAdapter());
            }
        };
        //null password
        new ExpectedFailure<NullPointerException>(null){
            @Override
            protected void run() throws Exception {
                new RemoteDataEmitter(DEFAULT_URL, "bla", null, new MyAdapter());
            }
        };
    }
    /**
     * Verifies connection failure when invalid address is specified.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void connectFailure() throws Exception {
        final MyAdapter adapter = new MyAdapter();
        Throwable jmsException = new ExpectedFailure<Exception>(null){
            @Override
            protected void run() throws Exception {
                new RemoteDataEmitter("tcp://localhost:101", "admin", "admin", adapter);
            }
        }.getException().getCause().getCause();
        assertTrue(jmsException.toString(),
                jmsException instanceof JMSException);
        assertTrue(jmsException.getCause().toString(),
                jmsException.getCause() instanceof ConnectException);

        assertTrue(adapter.hasNoObjects());
        assertTrue(adapter.hasNoStatus());

        //invalid URL
        new ExpectedFailure<Exception>(null) {
            @Override
            protected void run() throws Exception {
                new RemoteDataEmitter("this is not a URL", "admin", "admin", adapter);
            }
        };
        assertTrue(adapter.hasNoObjects());
        assertTrue(adapter.hasNoStatus());
    }

    /**
     * Connects to the receiver and disconnects
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void connectAndClose() throws Exception {
        //Init manager to create the receiver.
        initManager();
        //Now connect to it
        MyAdapter adapter = new MyAdapter();
        RemoteDataEmitter emitter = new RemoteDataEmitter(DEFAULT_URL,
                DEFAULT_CREDENTIAL, DEFAULT_CREDENTIAL, adapter);
        //Test whatever we can
        assertTrue(emitter.isConnected());
        assertNull(emitter.getLastFailure());
        assertTrue(adapter.toString(), adapter.hasNoObjects());
        assertFalse(adapter.toString(), adapter.hasNoStatus());
        assertEquals(new Pair<Boolean,Boolean>(false,true), adapter.getNextStatus());
        //Now close and test things
        emitter.close();
        assertFalse(emitter.isConnected());
        assertNull(emitter.getLastFailure());
        assertTrue(adapter.toString(), adapter.hasNoObjects());
        assertFalse(adapter.toString(), adapter.hasNoStatus());
        assertEquals(new Pair<Boolean,Boolean>(true,false), adapter.getNextStatus());
        //Do close again and verify that it makes no difference
        emitter.close();
        assertFalse(emitter.isConnected());
        assertNull(emitter.getLastFailure());
        assertTrue(adapter.toString(), adapter.hasNoObjects());
        assertTrue(adapter.toString(), adapter.hasNoStatus());
    }

    /**
     * Tests authentication failures when connecting to the strategy agent.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void authFailure() throws Exception {
        //Init manager to create the receiver.
        initManager();
        //Now verify different auth failures
        //empty user
        verifyAuthFailure("", DEFAULT_CREDENTIAL);
        //empty password
        verifyAuthFailure(DEFAULT_CREDENTIAL, "");
        //invalid user name password combination
        verifyAuthFailure("yes", "no");
    }

    /**
     * Receives data from the receiver.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void receiveData() throws Exception {
        //Init manager to create the receiver.
        initManager();
        //Now connect to it
        MyAdapter adapter = new MyAdapter();
        RemoteDataEmitter emitter = new RemoteDataEmitter(DEFAULT_URL,
                DEFAULT_CREDENTIAL, DEFAULT_CREDENTIAL, adapter);
        //Test whatever we can
        assertTrue(emitter.isConnected());
        assertFalse(adapter.toString(), adapter.hasNoStatus());
        assertEquals(new Pair<Boolean,Boolean>(false,true), adapter.getNextStatus());
        //Now create a data flow to supply objects to the receiver
        //The data to send
        Object [] data = {
                new AskEvent(1, 2, new Equity("asym"), "ex", BigDecimal.ONE, BigDecimal.TEN),
                new BidEvent(3, 4, new Equity("bsym"), "ex", BigDecimal.ONE, BigDecimal.TEN),
                new TradeEvent(5, 6, new Equity("csym"), "ex", BigDecimal.ONE, BigDecimal.TEN),
                new NonSerializable(),
                ClientTest.createOrderSingle(),
                ClientTest.createOrderReplace(),
                ClientTest.createOrderCancel(),
                ClientTest.createOrderFIX(),
                ClientTest.createCancelReject(),
                ClientTest.createExecutionReport(),
                org.marketcetera.core.notifications.Notification.high(
                        "Subject", "body", "test.notification"),
                BigInteger.ONE,
                "Test String"
        };
        //Now setup a data flow into the receiver.
        DataFlowID rFlowID = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(CopierModuleFactory.INSTANCE_URN, data),
                new DataRequest(ReceiverFactory.INSTANCE_URN)
        }, false);
        Object actual;
        for(Object expected: data) {
            if(expected instanceof NonSerializable) {
                //non serializable object shouldn't have been transmitted
                continue;
            }
            actual = adapter.getNextObject();
            if(expected instanceof OrderSingle) {
                TypesTestBase.assertOrderSingleEquals((OrderSingle)expected,
                        (OrderSingle)actual);
            } else if(expected instanceof OrderReplace) {
                TypesTestBase.assertOrderReplaceEquals((OrderReplace)expected,
                        (OrderReplace)actual);
            } else if(expected instanceof OrderCancel) {
                TypesTestBase.assertOrderCancelEquals((OrderCancel)expected,
                        (OrderCancel)actual);
            } else if(expected instanceof FIXOrder) {
                TypesTestBase.assertOrderFIXEquals((FIXOrder)expected,
                        (FIXOrder)actual);
            } else if(expected instanceof OrderCancelReject) {
                TypesTestBase.assertCancelRejectEquals((OrderCancelReject)expected,
                        (OrderCancelReject)actual);
            } else if(expected instanceof ExecutionReport) {
                TypesTestBase.assertExecReportEquals((ExecutionReport)expected,
                        (ExecutionReport)actual);
            } else if(expected instanceof org.marketcetera.core.notifications.Notification) {
                assertEquals(expected.toString(), actual.toString());
            } else {
                assertEquals(expected, actual);
            }
        }
        //Verify we have no extra objects
        assertTrue(adapter.toString(), adapter.hasNoObjects());
        //Verify we didn't get any failures etc.
        assertNull(emitter.getLastFailure());
        assertTrue(adapter.toString(), adapter.hasNoStatus());

        mManager.cancel(rFlowID);
    }


    /**
     * Tests connection status notifications when the receiver disconnects.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void connectionStatusNotify() throws Exception {
        mAssist.resetAppender();
        //Init manager to create the receiver.
        initManager();
        //Now connect to it
        MyAdapter adapter = new MyAdapter();
        RemoteDataEmitter emitter = new RemoteDataEmitter(DEFAULT_URL,
                DEFAULT_CREDENTIAL, DEFAULT_CREDENTIAL, adapter);
        //Test whatever we can
        assertTrue(emitter.isConnected());
        assertNull(emitter.getLastFailure());
        assertTrue(adapter.toString(), adapter.hasNoObjects());
        assertFalse(adapter.toString(), adapter.hasNoStatus());
        assertEquals(new Pair<Boolean,Boolean>(false,true), adapter.getNextStatus());
        //Stop the receiver to terminate the connection
        mManager.stop(ReceiverFactory.INSTANCE_URN);
        //wait for some time for the failure to be detected
        Thread.sleep(3000);
        //we should've received a connection failure notification by now
        assertFalse(adapter.toString(), adapter.hasNoStatus());
        assertEquals(new Pair<Boolean,Boolean>(true,false), adapter.getNextStatus());
        //And the emitter should be disconnected.
        assertFalse(emitter.isConnected());
        assertNotNull(emitter.getLastFailure());
        //Now close things and start again
        emitter.close();
        assertFalse(emitter.isConnected());
        assertNotNull(emitter.getLastFailure());
        assertTrue(adapter.toString(), adapter.hasNoObjects());
        assertTrue(adapter.toString(), adapter.hasNoStatus());
        // verify the logged warning.
        mAssist.assertLastEvent(Level.WARN,
                RemoteDataEmitter.class.getName(),
                Messages.LOG_ERROR_CLOSING_CONNECTION.getText(),
                null);

        //Start receiver
        mManager.start(ReceiverFactory.INSTANCE_URN);
        //and connect to it.
        adapter = new MyAdapter();
        emitter = new RemoteDataEmitter(DEFAULT_URL,
                DEFAULT_CREDENTIAL, DEFAULT_CREDENTIAL, adapter);
        //Verify that we are connected in every sense of the word.
        assertTrue(emitter.isConnected());
        assertNull(emitter.getLastFailure());
        assertTrue(adapter.toString(), adapter.hasNoObjects());
        assertFalse(adapter.toString(), adapter.hasNoStatus());
        assertEquals(new Pair<Boolean,Boolean>(false,true), adapter.getNextStatus());
        //Close the connection and verify that we get a notification
        emitter.close();
        assertFalse(emitter.isConnected());
        assertNull(emitter.getLastFailure());
        assertTrue(adapter.toString(), adapter.hasNoObjects());
        assertFalse(adapter.toString(), adapter.hasNoStatus());
        assertEquals(new Pair<Boolean,Boolean>(true,false), adapter.getNextStatus());
    }

    /**
     * Tests failures when deserializing objects.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void nonDeserializables() throws Exception {
        //Init manager to create the receiver.
        initManager();
        //Now connect to it
        MyAdapter adapter = new MyAdapter();
        RemoteDataEmitter emitter = new RemoteDataEmitter(DEFAULT_URL,
                DEFAULT_CREDENTIAL, DEFAULT_CREDENTIAL, adapter);
        //Test whatever we can
        assertTrue(emitter.isConnected());
        assertFalse(adapter.toString(), adapter.hasNoStatus());
        assertEquals(new Pair<Boolean,Boolean>(false,true), adapter.getNextStatus());
        //Now create a data flow to supply objects to the receiver
        //The data to send
        Object [] data = {
                "test",
                new NonDeserializable(),
                "once more"
        };
        //Now setup a data flow into the receiver.
        DataFlowID rFlowID = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(CopierModuleFactory.INSTANCE_URN, data),
                new DataRequest(ReceiverFactory.INSTANCE_URN)
        }, false);
        Object actual;
        for(Object expected: data) {
            if(expected instanceof NonDeserializable) {
                //non serializable object shouldn't have been transmitted
                continue;
            }
            actual = adapter.getNextObject();
            assertEquals(expected, actual);
        }
        //Verify we have no extra objects
        assertTrue(adapter.toString(), adapter.hasNoObjects());
        //Verify that we did get any serialization failures etc.
        assertNotNull(emitter.getLastFailure().toString(), emitter.getLastFailure());
        assertTrue(emitter.getLastFailure().toString(),
                emitter.getLastFailure().getCause() instanceof NotSerializableException);
        //This failure should result in the client be marked as closed
        assertFalse(emitter.isConnected());
        //And we should see a connection status notification for that.
        assertFalse(adapter.toString(), adapter.hasNoStatus());
        assertEquals(new Pair<Boolean,Boolean>(true,false), adapter.getNextStatus());

        mManager.cancel(rFlowID);
    }

    /**
     * Verifies the authentication failure for the specified user name
     * / password combination.
     *
     * @param inUsername the user name
     * @param inPassword the password
     *
     * @throws Exception if there were unexpected failures.
     */
    private void verifyAuthFailure(final String inUsername,
                                   final String inPassword)
            throws Exception {
        final MyAdapter adapter = new MyAdapter();
        Throwable failure = new ExpectedFailure<BeanCreationException>(null) {
            @Override
            protected void run() throws Exception {
                new RemoteDataEmitter(DEFAULT_URL, inUsername, inPassword, adapter);
            }
        }.getException();
        boolean isLoginError = false;
        do {
            if(failure instanceof LoginException) {
                isLoginError = true;
                break;
            }
        } while((failure = failure.getCause()) != null);
        assertTrue(isLoginError);
        assertTrue(adapter.hasNoObjects());
        assertTrue(adapter.hasNoStatus());
    }

    /**
     * Class for testing serialization failures.
     */
    private static class NonSerializable {
    }

    /**
     * Class for testing deserialization failures.
     */
    private static class NonDeserializable implements Serializable {
        private void readObject(ObjectInputStream in)throws IOException {
            //cause deserialization to fail.
            throw new NotSerializableException();
        }
        private static final long serialVersionUID = 1L;
    }

    /**
     * Adapter implementation for testing.
     */
    private static class MyAdapter implements EmitterAdapter {
        @Override
        public void receiveData(Object inObject) {
            mData.add(inObject);
        }

        @Override
        public void connectionStatusChanged(boolean inOldStatus, boolean inNewStatus) {
            mStatus.add(new Pair<Boolean, Boolean>(inOldStatus, inNewStatus));
        }

        /**
         * Returns the next received object. If no object is available, it
         * waits until an object is received.
         *
         * @return the received object.
         *
         * @throws InterruptedException if the wait for object to arrive was
         * interrupted.
         */
        public Object getNextObject() throws InterruptedException {
            return mData.take();
        }

        /**
         * Returns the next status update notification value. If no such
         * notification is available, the method wait untils such a
         * notification is received.
         *
         * @return the next status notification as a pair of old and new values.
         *
         * @throws InterruptedException if the wait for status notification to
         * arrive was interrupted.
         */
        public Pair<Boolean, Boolean> getNextStatus() throws InterruptedException {
            return mStatus.take();
        }

        /**
         * Returns true if no objects have been received so far.
         *
         * @return true if no objects have been received so far.
         */
        public boolean hasNoObjects() {
            return mData.isEmpty();
        }

        /**
         * Returns true if no status update notifications have been
         * received so far.
         *
         * @return true, if no status update notifications have been
         * received so far.
         */
        public boolean hasNoStatus() {
            return mStatus.isEmpty();
        }

        @Override
        public String toString() {
            return "MyAdapter{" +
                    "mData=" + mData +
                    ", mStatus=" + mStatus +
                    '}';
        }

        private final BlockingQueue<Object> mData =
                new LinkedBlockingQueue<Object>();
        private final BlockingQueue<Pair<Boolean,Boolean>> mStatus =
                new LinkedBlockingQueue<Pair<Boolean, Boolean>>();
    }

    private final LogTestAssist mAssist = new LogTestAssist(
            RemoteDataEmitter.class.getName(), Level.WARN);
}
