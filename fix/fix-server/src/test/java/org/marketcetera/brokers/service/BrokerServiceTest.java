package org.marketcetera.brokers.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.brokers.Broker;
import org.marketcetera.core.CoreException;
import org.marketcetera.fix.core.Messages;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Maps;

import quickfix.SessionID;

/* $License$ */

/**
 * Tests {@link BrokerServiceImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:**/test.xml" })
public class BrokerServiceTest
{
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        initiators = Maps.newHashMap();
        acceptors = Maps.newHashMap();
        for(Broker broker : brokerService.getBrokers()) {
            if(broker.getFixSession().isAcceptor()) {
                acceptors.put(broker.getBrokerId(),
                              broker);
            } else {
                initiators.put(broker.getBrokerId(),
                               broker);
            }
        }
    }
    /**
     * Test the ability to get brokers or a single broker.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetBrokers()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                brokerService.getBroker((SessionID)null);
            }
        };
        assertNull(brokerService.getBroker(new SessionID(FIXVersion.FIX42.getVersion(),
                                                         "not-a-sender",
                                                         "not-a-target")));
        Collection<Broker> brokers = brokerService.getBrokers();
        assertEquals(4,
                     brokers.size());
        Broker sampleBroker = brokers.iterator().next();
        Broker sampleBrokerCopy = brokerService.getBroker(new SessionID(sampleBroker.getFixSession().getSessionId()));
        assertEquals(sampleBroker.getBrokerId(),
                     sampleBrokerCopy.getBrokerId());
    }
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
        Broker initiator = initiators.values().iterator().next();
        final OrderSingle testOrder = Factory.getInstance().createOrderSingle();
        testOrder.setBrokerID(initiator.getBrokerId());
        testOrder.setInstrument(new Equity("METC"));
        testOrder.setOrderType(OrderType.Market);
        testOrder.setQuantity(BigDecimal.TEN);
        testOrder.setSide(Side.Buy);
        assertEquals(initiator.getBrokerId(),
                     brokerService.selectBroker(testOrder).getBrokerId());
        // test with an invalid broker
        testOrder.setBrokerID(new BrokerID("not-a-real-broker"));
        new ExpectedFailure<CoreException>(Messages.NO_BROKER_SELECTED) {
            @Override
            protected void run()
                    throws Exception
            {
                brokerService.selectBroker(testOrder);
            }
        };
        TestBrokerSelector testSelector = applicationContext.getBean(TestBrokerSelector.class);
        testSelector.setSelectedBrokerId(initiator.getBrokerId());
        testOrder.setBrokerID(null);
        assertEquals(initiator.getBrokerId(),
                     brokerService.selectBroker(testOrder).getBrokerId());
        // test when the selector throws an exception
        testSelector.setChooseBrokerException(new RuntimeException("This exception was expected"));
        new ExpectedFailure<RuntimeException>("This exception was expected") {
            @Override
            protected void run()
                    throws Exception
            {
                brokerService.selectBroker(testOrder);
            }
        };
    }
    /**
     * initiator brokers
     */
    private Map<BrokerID,Broker> initiators;
    /**
     * acceptor brokers
     */
    private Map<BrokerID,Broker> acceptors;
    /**
     * test broker service
     */
    @Autowired
    private BrokerServiceImpl brokerService;
    /**
     * test application context
     */
    @Autowired
    private ApplicationContext applicationContext;
}
