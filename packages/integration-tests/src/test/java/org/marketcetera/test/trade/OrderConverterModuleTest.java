package org.marketcetera.test.trade;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Deque;

import org.junit.Test;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.modules.headwater.HeadwaterModule;
import org.marketcetera.test.IntegrationTestBase;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.modules.OrderConverterModule;
import org.marketcetera.trade.modules.OwnedOrder;
import org.marketcetera.trade.service.TestBrokerSelector;

import com.google.common.collect.Lists;

import quickfix.Message;

/* $License$ */

/**
 * Test {@link OrderConverterModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OrderConverterModuleTest
        extends IntegrationTestBase
{
    /**
     * Test the wrong data type.
     *
     * @throws Exception if an unexpected failure occurs
     */
    @Test
    public void testWrongDataType()
            throws Exception
    {
        String headwaterInstance = generateHeadwaterInstanceName();
        Deque<Object> receivedData = Lists.newLinkedList();
        DataFlowID dataFlow = moduleManager.createDataFlow(getOrderConverterDataRequest(headwaterInstance,
                                                                                        receivedData));
        HeadwaterModule.getInstance(headwaterInstance).emit(this,
                                                            dataFlow);
        assertTrue(receivedData.isEmpty());
    }
    /**
     * Test that an order can be targeted and converted for a specific broker.
     *
     * @throws Exception if an unexpected failure occurs
     */
    @Test
    public void testSpecifiedBroker()
            throws Exception
    {
        ActiveFixSession target = null;
        for(ActiveFixSession broker : brokerService.getActiveFixSessions()) {
            if(!broker.getFixSession().isAcceptor() && broker.getFixSession().getMappedBrokerId() == null) {
                target = broker;
                break;
            }
        }
        makeBrokerAvailable(new BrokerID(target.getFixSession().getBrokerId()));
        final OrderSingle testOrder = Factory.getInstance().createOrderSingle();
        testOrder.setBrokerID(new BrokerID(target.getFixSession().getBrokerId()));
        testOrder.setInstrument(new Equity("METC"));
        testOrder.setOrderType(OrderType.Market);
        testOrder.setQuantity(BigDecimal.TEN);
        testOrder.setSide(Side.Buy);
        String headwaterInstance = generateHeadwaterInstanceName();
        Deque<Object> receivedData = Lists.newLinkedList();
        DataFlowID dataFlow = moduleManager.createDataFlow(getOrderConverterDataRequest(headwaterInstance,
                                                                                        receivedData));
        HeadwaterModule.getInstance(headwaterInstance).emit(new OwnedOrder(generateUser(),
                                                                           testOrder),
                                                            dataFlow);
        waitForMessages(1,
                        receivedData);
        Message convertedMessage = ((HasFIXMessage)receivedData.getFirst()).getMessage();
        assertNotNull(convertedMessage);
    }
    /**
     * Test that an order can be targeted and converted with no broker selected.
     *
     * @throws Exception if an unexpected failure occurs
     */
    @Test
    public void testNoSpecifiedBroker()
            throws Exception
    {
        ActiveFixSession target = null;
        for(ActiveFixSession broker : brokerService.getActiveFixSessions()) {
            if(!broker.getFixSession().isAcceptor() && broker.getFixSession().getMappedBrokerId() == null) {
                target = broker;
                break;
            }
        }
        makeBrokerAvailable(new BrokerID(target.getFixSession().getBrokerId()));
        selector.setSelectedBrokerId(new BrokerID(target.getFixSession().getBrokerId()));
        selector.setChooseBrokerException(null);
        final OrderSingle testOrder = Factory.getInstance().createOrderSingle();
        testOrder.setInstrument(new Equity("METC"));
        testOrder.setOrderType(OrderType.Market);
        testOrder.setQuantity(BigDecimal.TEN);
        testOrder.setSide(Side.Buy);
        String headwaterInstance = generateHeadwaterInstanceName();
        Deque<Object> receivedData = Lists.newLinkedList();
        DataFlowID dataFlow = moduleManager.createDataFlow(getOrderConverterDataRequest(headwaterInstance,
                                                                                        receivedData));
        HeadwaterModule.getInstance(headwaterInstance).emit(new OwnedOrder(generateUser(),
                                                                           testOrder),
                                                            dataFlow);
        waitForMessages(1,
                        receivedData);
        Message convertedMessage = ((HasFIXMessage)receivedData.getFirst()).getMessage();
        assertNotNull(convertedMessage);
    }
    /**
     * Test the behavior of a broker selection exception.
     *
     * @throws Exception if an unexpected failure occurs
     */
    @Test
    public void testSelectionException()
            throws Exception
    {
        ActiveFixSession target = null;
        for(ActiveFixSession broker : brokerService.getActiveFixSessions()) {
            if(!broker.getFixSession().isAcceptor() && broker.getFixSession().getMappedBrokerId() == null) {
                target = broker;
                break;
            }
        }
        makeBrokerAvailable(new BrokerID(target.getFixSession().getBrokerId()));
        TestBrokerSelector selector = applicationContext.getBean(TestBrokerSelector.class);
        selector.setSelectedBrokerId(new BrokerID(target.getFixSession().getBrokerId()));
        selector.setChooseBrokerException(new RuntimeException("This exception is expected"));
        final OrderSingle testOrder = Factory.getInstance().createOrderSingle();
        testOrder.setInstrument(new Equity("METC"));
        testOrder.setOrderType(OrderType.Market);
        testOrder.setQuantity(BigDecimal.TEN);
        testOrder.setSide(Side.Buy);
        String headwaterInstance = generateHeadwaterInstanceName();
        Deque<Object> receivedData = Lists.newLinkedList();
        DataFlowID dataFlow = moduleManager.createDataFlow(getOrderConverterDataRequest(headwaterInstance,
                                                                                        receivedData));
        HeadwaterModule.getInstance(headwaterInstance).emit(new OwnedOrder(generateUser(),
                                                                           testOrder),
                                                            dataFlow);
        assertTrue(receivedData.isEmpty());
    }
}
