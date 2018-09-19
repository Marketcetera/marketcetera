package org.marketcetera.test.trade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;
import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.core.CoreException;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.ServerFixSession;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.test.IntegrationTestBase;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.service.FieldSetterMessageModifier;
import org.marketcetera.trade.service.Messages;
import org.marketcetera.trade.service.TestBrokerSelector;
import org.marketcetera.trade.service.impl.TradeServiceImpl;

import com.google.common.collect.Maps;

import quickfix.Message;

/* $License$ */

/**
 * Tests {@link TradeServiceImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TradeServiceImplTest
        extends IntegrationTestBase
{
    /**
     * Test the ability to select a broker for an order.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testSelectBroker()
            throws Exception
    {
        // test order with a specified broker
        ActiveFixSession initiator = null;
        for(ActiveFixSession fixSession : brokerService.getActiveFixSessions()) {
            if(!fixSession.getFixSession().isAcceptor()) {
                initiator = fixSession;
                break;
            }
        }
        final OrderSingle testOrder = Factory.getInstance().createOrderSingle();
        testOrder.setBrokerID(new BrokerID(initiator.getFixSession().getBrokerId()));
        testOrder.setInstrument(new Equity("METC"));
        testOrder.setOrderType(OrderType.Market);
        testOrder.setQuantity(BigDecimal.TEN);
        testOrder.setSide(Side.Buy);
        assertEquals(initiator.getFixSession().getBrokerId(),
                     tradeService.selectServerFixSession(testOrder).getActiveFixSession().getFixSession().getBrokerId());
        // test with an invalid broker
        selector.setChooseBrokerException(null);
        selector.setSelectedBrokerId(null);
        testOrder.setBrokerID(new BrokerID("not-a-real-broker"));
        new ExpectedFailure<CoreException>(Messages.NO_BROKER_SELECTED) {
            @Override
            protected void run()
                    throws Exception
            {
                tradeService.selectServerFixSession(testOrder);
            }
        };
        TestBrokerSelector testSelector = applicationContext.getBean(TestBrokerSelector.class);
        testSelector.setSelectedBrokerId(new BrokerID(initiator.getFixSession().getBrokerId()));
        testOrder.setBrokerID(null);
        assertEquals(initiator.getFixSession().getBrokerId(),
                     tradeService.selectServerFixSession(testOrder).getActiveFixSession().getFixSession().getBrokerId());
        // test when the selector throws an exception
        testSelector.setChooseBrokerException(new RuntimeException("This exception was expected"));
        new ExpectedFailure<RuntimeException>("This exception was expected") {
            @Override
            protected void run()
                    throws Exception
            {
                tradeService.selectServerFixSession(testOrder);
            }
        };
    }
    /**
     * Test that order modifiers get applied.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testBrokerModifier()
            throws Exception
    {
        ServerFixSession target = null;
        for(ServerFixSession fixSession : brokerService.getServerFixSessions()) {
            if(!fixSession.getActiveFixSession().getFixSession().isAcceptor() && !fixSession.getOrderModifiers().isEmpty() && fixSession.getActiveFixSession().getFixSession().getMappedBrokerId() == null) {
                target = fixSession;
                break;
            }
        }
        BrokerID targetBrokerId = new BrokerID(target.getActiveFixSession().getFixSession().getBrokerId());
        assertNotNull("No initiator test session with order modifiers",
                      target);
        makeBrokerAvailable(targetBrokerId);
        // send an order through and make sure the modifiers are applied
        OrderSingle testOrder = Factory.getInstance().createOrderSingle();
        testOrder.setBrokerID(targetBrokerId);
        testOrder.setInstrument(new Equity("METC"));
        testOrder.setOrderType(OrderType.Market);
        testOrder.setQuantity(BigDecimal.TEN);
        testOrder.setSide(Side.Buy);
        assertNull(testOrder.getCustomFields());
        Message convertedOrder = tradeService.convertOrder(testOrder,
                                                           target);
        Map<Integer,String> expectedCustomFields = Maps.newHashMap();
        for(MessageModifier orderModifier : target.getOrderModifiers()) {
            if(orderModifier instanceof FieldSetterMessageModifier) {
                FieldSetterMessageModifier fieldSetter = (FieldSetterMessageModifier)orderModifier;
                expectedCustomFields.put(fieldSetter.getField(),
                                         fieldSetter.getValue());
            }
        }
        assertFalse("This test expects at least one FieldSetterMessageModifier",
                    expectedCustomFields.isEmpty());
        for(Map.Entry<Integer,String> entry : expectedCustomFields.entrySet()) {
            int field = entry.getKey();
            String value = entry.getValue();
            assertTrue("Expected to find field " + field + " on " + convertedOrder,
                       convertedOrder.isSetField(field));
            assertEquals("Expected field " + field + " to be '" + value + "' on " + convertedOrder,
                         value,
                         convertedOrder.getString(field));
        }
    }
    /**
     * Test mapped broker selector.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testMappedBroker()
            throws Exception
    {
        ActiveFixSession virtualSession = null;
        for(ActiveFixSession session : brokerService.getActiveFixSessions()) {
            if(session.getFixSession().getMappedBrokerId() != null) {
                virtualSession = session;
                break;
            }
        }
        assertNotNull("No virtual brokers in test broker settings",
                      virtualSession);
        BrokerID virtualSessionBrokerId = new BrokerID(virtualSession.getFixSession().getBrokerId());
        OrderSingle testOrder = Factory.getInstance().createOrderSingle();
        testOrder.setBrokerID(virtualSessionBrokerId);
        testOrder.setInstrument(new Equity("METC"));
        testOrder.setOrderType(OrderType.Market);
        testOrder.setQuantity(BigDecimal.TEN);
        testOrder.setSide(Side.Buy);
        assertEquals(virtualSession.getFixSession().getBrokerId(),
                     tradeService.selectServerFixSession(testOrder).getActiveFixSession().getFixSession().getBrokerId());
    }
    /**
     * Test mapped broker order modifiers.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testMappedBrokerModifiers()
            throws Exception
    {
        ServerFixSession virtualSession = null;
        for(ServerFixSession session : brokerService.getServerFixSessions()) {
            if(session.getActiveFixSession().getFixSession().getMappedBrokerId() != null) {
                virtualSession = session;
                break;
            }
        }
        assertNotNull("No virtual brokers in test broker settings",
                      virtualSession);
        BrokerID virtualBrokerId = new BrokerID(virtualSession.getActiveFixSession().getFixSession().getMappedBrokerId());
        ServerFixSession mappedSession = brokerService.getServerFixSession(virtualBrokerId);
        assertNotNull(mappedSession);
        BrokerID mappedBrokerId = new BrokerID(mappedSession.getActiveFixSession().getFixSession().getBrokerId());
        makeBrokerAvailable(virtualBrokerId);
        makeBrokerAvailable(mappedBrokerId);
        OrderSingle testOrder = Factory.getInstance().createOrderSingle();
        testOrder.setBrokerID(virtualBrokerId);
        testOrder.setInstrument(new Equity("METC"));
        testOrder.setOrderType(OrderType.Market);
        testOrder.setQuantity(BigDecimal.TEN);
        testOrder.setSide(Side.Buy);
        assertNull(testOrder.getCustomFields());
        Message convertedOrder = tradeService.convertOrder(testOrder,
                                                           virtualSession);
        Map<Integer,String> expectedCustomFields = Maps.newHashMap();
        // apply virtual modifiers first
        for(MessageModifier orderModifier : virtualSession.getOrderModifiers()) {
            if(orderModifier instanceof FieldSetterMessageModifier) {
                FieldSetterMessageModifier fieldSetter = (FieldSetterMessageModifier)orderModifier;
                expectedCustomFields.put(fieldSetter.getField(),
                                         fieldSetter.getValue());
            }
        }
        for(MessageModifier orderModifier : mappedSession.getOrderModifiers()) {
            if(orderModifier instanceof FieldSetterMessageModifier) {
                FieldSetterMessageModifier fieldSetter = (FieldSetterMessageModifier)orderModifier;
                expectedCustomFields.put(fieldSetter.getField(),
                                         fieldSetter.getValue());
            }
        }
        assertFalse("This test expects at least one FieldSetterMessageModifier",
                    expectedCustomFields.isEmpty());
        for(Map.Entry<Integer,String> entry : expectedCustomFields.entrySet()) {
            int field = entry.getKey();
            String value = entry.getValue();
            assertTrue("Expected to find field " + field + " on " + convertedOrder,
                       convertedOrder.isSetField(field));
            assertEquals("Expected field " + field + " to be '" + value + "' on " + convertedOrder,
                         value,
                         convertedOrder.getString(field));
        }
    }
}
