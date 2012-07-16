package org.marketcetera.ors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.NewFixVersionedTestCase;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.mockito.ArgumentMatcher;

import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.MsgType;
import quickfix.field.OrigClOrdID;

/* $License$ */

/**
 * Tests {@link ReplyPersister}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ReplyPersisterTest
        extends NewFixVersionedTestCase
{
    /**
     * Create a new ReplyPersisterTest instance.
     *
     * @param inVersion a <code>FIXVersion</code> value
     */
    public ReplyPersisterTest(FIXVersion inVersion)
    {
        super(inVersion);
    }
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        super.setup();
        reportHistoryServices = mock(ReportHistoryServices.class);
        orderInfoCache = mock(OrderInfoCache.class);
        replyPersister = new ReplyPersister(reportHistoryServices,
                                            orderInfoCache);
    }
    /**
     * Tests {@link ReplyPersister#getPrincipals(Message, boolean)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetPrincipals()
            throws Exception
    {
        // there are three states for the OrderID and three states for the OrigOrderID: not on the order, on the order and not in the cache, on the order and in the cache
        // this test verifies the results of each
        doOneGetPrincipalsTest(null,
                               null);
        doOneGetPrincipalsTest(null,
                               "order-" + counter.incrementAndGet());
        doOneGetPrincipalsTest("order-" + counter.incrementAndGet(),
                               null);
        doOneGetPrincipalsTest("order-" + counter.incrementAndGet(),
                               "order-" + counter.incrementAndGet());
    }
    /**
     * Executes a single iteration of the get principals test. 
     *
     * @param inOrderID a <code>String</code> value
     * @param inOrigOrderID a <code>String</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doOneGetPrincipalsTest(String inOrderID,
                                        String inOrigOrderID)
            throws Exception
    {
        Message executionReport = generateExecutionReport(inOrderID,
                                                          inOrigOrderID);
        SLF4JLoggerProxy.debug(ReplyPersisterTest.class,
                               "Testing orderID: {} origOrderID: {} using {}",
                               inOrderID,
                               inOrigOrderID,
                               executionReport);
        Principals expectedPrincipals = Principals.UNKNOWN;
        // the first time we check, we'll always get Principals.UNKNOWN back because we haven't set the cache to return anything
        Principals actualPrincipals = replyPersister.getPrincipals(executionReport,
                                                                   true);
        validatePrincipals(Principals.UNKNOWN,
                           actualPrincipals);
        // now, if either of the order values are non-null, set up the cache to return appropriately
        OrderID orderID = inOrderID == null ? null : new OrderID(inOrderID);
        OrderID origOrderID = inOrigOrderID == null ? null : new OrderID(inOrigOrderID);
        OrderID orderIDToCache = null;
        if(inOrderID != null) {
            orderIDToCache = new OrderID(inOrderID);
        } else {
            if(inOrigOrderID != null) {
                orderIDToCache = new OrderID(inOrigOrderID);
            }
        }
        if(orderIDToCache != null) {
            expectedPrincipals = generatePrincipals();
            // now set up the orderInfo we want to use to create the principals
            OrderInfo orderInfo = mock(OrderInfo.class);
            when(orderInfo.getActorID()).thenReturn(expectedPrincipals.getActorID());
            when(orderInfo.getViewerID()).thenReturn(expectedPrincipals.getViewerID());
            when(orderInfo.getOrderID()).thenReturn(orderID);
            when(orderInfo.getOrigOrderID()).thenReturn(origOrderID);
            when(orderInfo.isViewerIDSet()).thenReturn(true);
            // set up the order info cache to return an order info object we want
            when(orderInfoCache.get(argThat(new OrderIDMatcher(orderIDToCache)))).thenReturn(orderInfo);
        }
        actualPrincipals = replyPersister.getPrincipals(executionReport,
                                                        true);
        validatePrincipals(expectedPrincipals,
                           actualPrincipals);
    }
    /**
     * Generates a <code>Principals</code> value guaranteed to be unique.
     *
     * @return a <code>Principals</code> value
     */
    private Principals generatePrincipals()
    {
        return new Principals(new UserID(counter.incrementAndGet()),
                              new UserID(counter.incrementAndGet()));
    }
    /**
     * Validates that the given expected value matches the given actual value.
     *
     * @param inExpectedValue a <code>Principals</code> value
     * @param inActualValue a <code>Principals</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void validatePrincipals(Principals inExpectedValue,
                                    Principals inActualValue)
            throws Exception
    {
        if(inExpectedValue == null) {
            assertNull(inActualValue);
        } else {
            assertNotNull(inActualValue);
        }
        assertEquals(inExpectedValue.getActorID(),
                     inActualValue.getActorID());
        assertEquals(inExpectedValue.getViewerID(),
                     inActualValue.getViewerID());
    }
    /**
     * Generates an <code>ExecutionReport</code> with the given <code>ClOrdID</code> and <code>ClOrigOrdID</code> values.
     *
     * @param inOrderID a <code>String</code> or <code>null</code>
     * @param inOrigOrderID a <code>String</code> or <code>null</code>
     * @return a <code>Message</code> value
     */
    private Message generateExecutionReport(String inOrderID,
                                            String inOrigOrderID)
    {
        Message message = getMsgFactory().createMessage(MsgType.EXECUTION_REPORT);
        if(inOrderID != null) {
            message.setField(new ClOrdID(inOrderID));
        }
        if(inOrigOrderID != null) {
            message.setField(new OrigClOrdID(inOrigOrderID));
        }
        return message;
    }
    /**
     * Provides an <code>ArgumentMatcher</code> that matches a given <code>OrderID</code>.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class OrderIDMatcher
            extends ArgumentMatcher<OrderID>
    {
        /**
         * Create a new OrderIDMatcher instance.
         *
         * @param inOrderID an <code>OrderID</code> value
         */
        private OrderIDMatcher(OrderID inOrderID)
        {
            orderID = inOrderID;
        }
        /* (non-Javadoc)
         * @see org.mockito.ArgumentMatcher#matches(java.lang.Object)
         */
        @Override
        public boolean matches(Object inArgument)
        {
            return orderID.equals(inArgument);
        }
        /**
         * orderID value to match
         */
        private final OrderID orderID;
    }
    /**
     * generates unique identifiers
     */
    private AtomicLong counter = new AtomicLong(0);
    /**
     * test replyPersister object
     */
    private ReplyPersister replyPersister;
    /**
     * test report history services provider value
     */
    private ReportHistoryServices reportHistoryServices;
    /**
     * test order info cache value
     */
    private OrderInfoCache orderInfoCache;
}
