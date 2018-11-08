package com.marketcetera.ors.ws;

import org.apache.commons.lang.Validate;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;

import com.marketcetera.ors.brokers.BrokerService;

/* $License$ */

/**
 * Provides a cluster-aware <code>ServiceProvider</code> implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ClusteredServiceProvider.java 17266 2017-04-28 14:58:00Z colin $
 * @since 2.5.0
 */
@ClassVersion("$Id: ClusteredServiceProvider.java 17266 2017-04-28 14:58:00Z colin $")
public class ClusteredServiceProvider
        extends ServiceProvider
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.ws.ServiceProvider#start()
     */
    @Override
    public void start()
    {
        Validate.notNull(brokerService);
        super.start();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.ws.ServiceProvider#getBrokersStatus(java.lang.String)
     */
    @Override
    public BrokersStatus getBrokersStatus(String inUsername)
    {
        return brokerService.getBrokersStatus();
    }
    /**
     * Get the brokerService value.
     *
     * @return a <code>BrokerService</code> value
     */
    public BrokerService getBrokerService()
    {
        return brokerService;
    }
    /**
     * Sets the brokerService value.
     *
     * @param inBrokerService a <code>BrokerService</code> value
     */
    public void setBrokerService(BrokerService inBrokerService)
    {
        brokerService = inBrokerService;
    }
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
}
