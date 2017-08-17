package org.marketcetera.trade.service.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.brokers.Broker;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.core.CoreException;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.service.Messages;
import org.marketcetera.trade.service.TestBrokerSelector;
import org.marketcetera.trade.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/* $License$ */

/**
 * Tests {@link TradeServiceImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:**/test.xml" })
public class TradeServiceImplTest
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
        Broker initiator = null;
        for(Broker broker : brokerService.getBrokers()) {
            if(!broker.getFixSession().isAcceptor()) {
                initiator = broker;
                break;
            }
        }
        final OrderSingle testOrder = Factory.getInstance().createOrderSingle();
        testOrder.setBrokerID(initiator.getBrokerId());
        testOrder.setInstrument(new Equity("METC"));
        testOrder.setOrderType(OrderType.Market);
        testOrder.setQuantity(BigDecimal.TEN);
        testOrder.setSide(Side.Buy);
        assertEquals(initiator.getBrokerId(),
                     tradeService.selectBroker(testOrder).getBrokerId());
        // test with an invalid broker
        testOrder.setBrokerID(new BrokerID("not-a-real-broker"));
        new ExpectedFailure<CoreException>(Messages.NO_BROKER_SELECTED) {
            @Override
            protected void run()
                    throws Exception
            {
                tradeService.selectBroker(testOrder);
            }
        };
        TestBrokerSelector testSelector = applicationContext.getBean(TestBrokerSelector.class);
        testSelector.setSelectedBrokerId(initiator.getBrokerId());
        testOrder.setBrokerID(null);
        assertEquals(initiator.getBrokerId(),
                     tradeService.selectBroker(testOrder).getBrokerId());
        // test when the selector throws an exception
        testSelector.setChooseBrokerException(new RuntimeException("This exception was expected"));
        new ExpectedFailure<RuntimeException>("This exception was expected") {
            @Override
            protected void run()
                    throws Exception
            {
                tradeService.selectBroker(testOrder);
            }
        };
    }
    /**
     * test application context
     */
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * test broker service
     */
    @Autowired
    private BrokerService brokerService;
    /**
     * test trade service
     */
    @Autowired
    private TradeService tradeService;
}
