package org.marketcetera.trade.service;

import static org.junit.Assert.assertNotNull;

import org.marketcetera.brokers.Broker;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.trade.BrokerID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class TradeTestBase
{
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
     * test application context
     */
    @Autowired
    protected ApplicationContext applicationContext;
    /**
     * test broker service
     */
    @Autowired
    protected BrokerService brokerService;
    /**
     * test trade service
     */
    @Autowired
    protected TradeService tradeService;
    /**
     * provides access to cluster services
     */
    @Autowired
    protected ClusterService clusterService;
}
