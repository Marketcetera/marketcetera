package org.marketcetera.saclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import org.marketcetera.client.ClientImplTest;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.module.CopierModuleFactory;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.modules.remote.receiver.ReceiverFactory;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.TypesTestBase;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Tests {@link SAClient} JMS functions.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class SAClientJMSTest extends SAClientTestBase {
    /**
     * Tests the behavior when a null receiver is added / removed.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void nullReceiver() throws Exception {
        new ExpectedFailure<NullPointerException>(){
            @Override
            protected void run() throws Exception {
                getClient().addDataReceiver(null);
            }
        };
        new ExpectedFailure<NullPointerException>(){
            @Override
            protected void run() throws Exception {
                getClient().removeDataReciever(null);
            }
        };
    }
    /**
     * Tests simple reception of data by a single receiver.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test(timeout = 1000000)
    public void simpleReceive() throws Exception {
        getClient().addDataReceiver(mReceiver1);
        runTest(new TestVerifier() {
            @Override
            public void verifyExpected(Object inExpected) throws Exception {
                verifyEquals(inExpected, mReceiver1.getNext());
            }
        });
    }

    /**
     * Tests reception of data by two receivers and verifies that the
     * order of data delivery to the two receivers.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test(timeout = 10000)
    public void dualReceivers() throws Exception {
        getClient().addDataReceiver(mReceiver1);
        getClient().addDataReceiver(mReceiver2);
        runTest(new TestVerifier() {
            @Override
            public void verifyExpected(Object inExpected) throws Exception {
                verifyEquals(inExpected, mReceiver1.getNext());
                //since receiver2 receives data before receiver1, receiver2
                //should have data by now.
                assertTrue(mReceiver2.hasData());
                verifyEquals(inExpected, mReceiver2.getNext());
            }
        });
        //verify that receiver1 indeed received data after receiver2
        assertThat(mReceiver1.getLastAddTime(),
                Matchers.greaterThan(mReceiver2.getLastAddTime()));
    }

    /**
     * Verifies that failure of a receiver to receive data does not
     * impact the delivery of data to other receivers.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test(timeout = 10000)
    public void receiverFailure() throws Exception {
        //Configure both the receivers to fail
        mReceiver1.setFail(true);
        mReceiver2.setFail(true);
        getClient().addDataReceiver(mReceiver1);
        getClient().addDataReceiver(mReceiver2);
        runTest(new TestVerifier() {
            @Override
            public void verifyExpected(Object inExpected) throws Exception {
                verifyEquals(inExpected, mReceiver1.getNext());
                //since receiver2 receives data before receiver1, receiver2
                //should have data by now.
                assertTrue(mReceiver2.hasData());
                verifyEquals(inExpected, mReceiver2.getNext());
            }
        });
        //As a double check verify that receivers fail when setFail is set
        new ExpectedFailure<IllegalStateException>() {
            @Override
            protected void run() throws Exception {
                mReceiver1.receiveData(new Object());
            }
        };
    }

    /**
     * Verifies that a receiver does not receive any data after it has been
     * removed.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test(timeout = 10000)
    public void noReceiveOnRemove() throws Exception {
        getClient().addDataReceiver(mReceiver1);
        getClient().addDataReceiver(mReceiver2);
        getClient().removeDataReciever(mReceiver1);
        runTest(new TestVerifier() {
            @Override
            public void verifyExpected(Object inExpected) throws Exception {
                verifyEquals(inExpected, mReceiver2.getNext());
            }
        });
    }

    /**
     * Verfies that lack of any data receivers does not impact data flows.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test(timeout = 10000)
    public void noReceivers() throws Exception {
        runTest(new TestVerifier() {
            @Override
            public void verifyExpected(Object inExpected) throws Exception {
                //Slow it down to allow for data to be received if it can be.
                Thread.sleep(300);
            }
        });

    }

    /**
     * Verifies that when all receivers are removed, no data is received.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test(timeout = 10000)
    public void allReceiversRemoved() throws Exception {
        getClient().addDataReceiver(mReceiver1);
        getClient().addDataReceiver(mReceiver2);
        getClient().removeDataReciever(mReceiver1);
        getClient().removeDataReciever(mReceiver2);
        //Remove a receiver that was never added for kicks.
        getClient().removeDataReciever(new MyDataReceiver());
        runTest(new TestVerifier() {
            @Override
            public void verifyExpected(Object inExpected) throws Exception {
                //Slow it down to allow for data to be received if it can be.
                Thread.sleep(300);
            }
        });

    }

    /**
     * Verifies that a receiver receives data twice if it's added twice.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test(timeout = 10000)
    public void receiverAddedTwice() throws Exception {
        getClient().addDataReceiver(mReceiver1);
        getClient().addDataReceiver(mReceiver1);
        runTest(new TestVerifier() {
            @Override
            public void verifyExpected(Object inExpected) throws Exception {
                verifyEquals(inExpected, mReceiver1.getNext());
                verifyEquals(inExpected, mReceiver1.getNext());
            }
        });
    }

    /**
     * Verifies that when duplicate receivers are added, its remove removes
     * the most recently added instance.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test(timeout = 10000)
    public void duplicateReceiverRemove() throws Exception {
        getClient().addDataReceiver(mReceiver1);
        getClient().addDataReceiver(mReceiver2);
        //Add receiver1 twice.
        getClient().addDataReceiver(mReceiver1);
        //And then remove it
        getClient().removeDataReciever(mReceiver1);
        runTest(new TestVerifier() {
            @Override
            public void verifyExpected(Object inExpected) throws Exception {
                verifyEquals(inExpected, mReceiver1.getNext());
                //since receiver2 receives data before receiver1, receiver2
                //should have data by now.
                assertTrue(mReceiver2.hasData());
                verifyEquals(inExpected, mReceiver2.getNext());
            }
        });
        //verify that receiver1 indeed received data after receiver2
        //which verifies that most recently added instance was removed.
        //if the other instance was removed this assertion would fail.
        assertThat(mReceiver1.getLastAddTime(),
                Matchers.greaterThan(mReceiver2.getLastAddTime()));

    }

    /**
     * Removes the receivers and resets their state.
     */
    @After
    public void resetReceivers() {
        getClient().removeDataReciever(mReceiver1);
        getClient().removeDataReciever(mReceiver2);
        mReceiver1.reset();
        mReceiver2.reset();
    }

    /**
     * Runs a test data flow that results in the client receiving the result
     * of that data flow.
     *
     * @param inVerifier the verifier that performs the verification of
     * actual data received during the data flow.
     *
     * @throws Exception if there were unexpected errors.
     */
    private void runTest(TestVerifier inVerifier) throws Exception {
        Object[] data = createTestObjects();
        assertReceiversHaveNoData();
        //Create a flow to send data to the receiver module
        DataFlowID flowID = emitData(data);
        try {
            //verify that we receive all the data
            for (Object expected : data) {
                //Verify only if expected is non-null
                //null values are not transmitted
                if (expected != null) {
                    inVerifier.verifyExpected(expected);
                }
            }
        } finally {
            if (flowID != null) {
                getMM().cancel(flowID);
            }
        }
        assertReceiversHaveNoData();
    }

    /**
     * An interface to help with testing.
     */
    private static interface TestVerifier {
        /**
         * Verifies the actual data received is the same as the supplied
         * expected object.
         *
         * @param inExpected the expected object.
         *
         * @throws Exception if there were unexpected errors.
         */
        public void verifyExpected(Object inExpected) throws Exception;
    }

    /**
     * Verifies that both the receivers have no data.
     */
    private void assertReceiversHaveNoData() {
        assertFalse(mReceiver1.hasData());
        assertFalse(mReceiver2.hasData());
    }

    /**
     * Verifies the equality of the supplied objects. The equality check
     * is adapted based on the type of object.
     *
     * @param inExpected the expected object.
     * @param inActual the actual object.
     */
    private void verifyEquals(Object inExpected, Object inActual) {
        if (inExpected instanceof OrderSingle) {
            TypesTestBase.assertOrderSingleEquals((OrderSingle) inExpected,
                    (OrderSingle) inActual);
        } else if (inExpected instanceof OrderReplace) {
            TypesTestBase.assertOrderReplaceEquals((OrderReplace) inExpected,
                    (OrderReplace) inActual);
        } else if (inExpected instanceof OrderCancel) {
            TypesTestBase.assertOrderCancelEquals((OrderCancel) inExpected,
                    (OrderCancel) inActual);
        } else if (inExpected instanceof FIXOrder) {
            TypesTestBase.assertOrderFIXEquals((FIXOrder) inExpected,
                    (FIXOrder) inActual);
        } else if (inExpected instanceof OrderCancelReject) {
            TypesTestBase.assertCancelRejectEquals((OrderCancelReject) inExpected,
                    (OrderCancelReject) inActual);
        } else if (inExpected instanceof ExecutionReport) {
            TypesTestBase.assertExecReportEquals((ExecutionReport) inExpected,
                    (ExecutionReport) inActual);
        } else if (inExpected instanceof org.marketcetera.core.notifications.Notification) {
            assertEquals(inExpected.toString(), inActual.toString());
        } else {
            assertEquals(inExpected, inActual);
        }
    }

    /**
     * Creates test objects that will be streamed in the data flow for testing.
     *
     * @return the array of created objects.
     *
     * @throws Exception if there were unexpected errors.
     */
    private Object[] createTestObjects() throws Exception {
        return new Object[]{
                EventTestBase.generateEquityAskEvent(1, 2, new Equity("asym"), "ex", BigDecimal.ONE, BigDecimal.TEN),
                EventTestBase.generateEquityBidEvent(3, 4, new Equity("bsym"), "ex", BigDecimal.ONE, BigDecimal.TEN),
                EventTestBase.generateEquityTradeEvent(5, 6, new Equity("csym"), "ex", BigDecimal.ONE, BigDecimal.TEN),
                ClientImplTest.createOrderSingle(),
                ClientImplTest.createOrderReplace(),
                ClientImplTest.createOrderCancel(),
                ClientImplTest.createOrderFIX(),
                ClientImplTest.createCancelReject(),
                ClientImplTest.createExecutionReport(),
                org.marketcetera.core.notifications.Notification.high(
                        "Subject", "body", "test.notification"),
                BigInteger.ONE,
                null,
                "Test String"
        };
    }

    /**
     * Emits data into the receiver. So that it can be received by the client
     *
     * @param inData the data that needs to be emitted by the data flow.
     * @return the data flowID.
     * @throws Exception if there were unexpected errors.
     */
    private static DataFlowID emitData(Object... inData) throws Exception {
        return getMM().createDataFlow(new DataRequest[]{
                new DataRequest(CopierModuleFactory.INSTANCE_URN, inData),
                new DataRequest(ReceiverFactory.INSTANCE_URN)
        }, false);
    }

    private final MyDataReceiver mReceiver1 = new MyDataReceiver();
    private final MyDataReceiver mReceiver2 = new MyDataReceiver();
}
