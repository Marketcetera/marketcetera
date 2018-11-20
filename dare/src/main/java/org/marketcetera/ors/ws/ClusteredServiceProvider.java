package org.marketcetera.ors.ws;

import org.apache.commons.lang.Validate;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.ors.brokers.BrokerService;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;


/* $License$ */

/**
 * Provides a cluster-aware <code>ServiceProvider</code> implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.5.0
 */
@ClassVersion("$Id$")
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
