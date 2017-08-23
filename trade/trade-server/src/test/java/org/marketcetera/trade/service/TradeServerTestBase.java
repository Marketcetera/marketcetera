package org.marketcetera.trade.service;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.junit.Before;
import org.marketcetera.brokers.Broker;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.modules.fix.FixModuleTestBase;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Provides common behavior for trade tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class TradeServerTestBase
        extends FixModuleTestBase
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
        super.setup();
        Broker target = null;
        for(Broker broker : brokerService.getBrokers()) {
            if(!broker.getFixSession().isAcceptor() && broker.getMappedBrokerId() == null) {
                target = broker;
                break;
            }
        }
        assertNotNull(target);
        selector.setSelectedBrokerId(target.getBrokerId());
        selector.setChooseBrokerException(null);
    }
    /**
     * Makes the given broker available.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void makeBrokerAvailable(BrokerID inBrokerId)
            throws Exception
    {
        Broker broker = brokerService.getBroker(inBrokerId);
        assertNotNull(broker);
        reportBrokerStatus(broker,
                           FixSessionStatus.CONNECTED,
                           true);
    }
    /**
     * Reports the broker status as indicated.
     *
     * @param inBroker a <code>Broker</code> value
     * @param inFixSessionStatus a <code>FixSessionStatus</code> value
     * @param inIsLoggedOn a <code>boolean</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected void reportBrokerStatus(Broker inBroker,
                                    FixSessionStatus inFixSessionStatus,
                                    boolean inIsLoggedOn)
            throws Exception
    {
        brokerService.reportBrokerStatus(brokerService.generateBrokerStatus(inBroker.getFixSession(),
                                                                            clusterService.getInstanceData(),
                                                                            FixSessionStatus.CONNECTED,
                                                                            true));
    }
    /**
     * Generate a test order single.
     *
     * @return an <code>OrderSingle</code> value
     */
    protected OrderSingle generateOrder()
    {
        return generateOrder(null);
    }
    /**
     * Generate a test order single with the given broker.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     * @return an <code>OrderSingle</code> value
     */
    protected OrderSingle generateOrder(BrokerID inBrokerId)
    {
        OrderSingle order = Factory.getInstance().createOrderSingle();
        order.setBrokerID(inBrokerId);
        order.setInstrument(new Equity("METC"));
        order.setOrderType(OrderType.Market);
        order.setQuantity(BigDecimal.TEN);
        order.setSide(Side.Buy);
        return order;
    }
    /**
     * test trade service
     */
    @Autowired
    protected TradeService tradeService;
    /**
     * broker selector
     */
    @Autowired
    protected TestBrokerSelector selector;
}
