package org.marketcetera.marketdata.provider;

import javax.management.MXBean;

import org.marketcetera.marketdata.ProviderStatus;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 * Provides a management interface for a market data provider.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@MXBean(true)
public interface MarketDataProviderMBean
        extends Lifecycle
{
    /**
     * Gets the total number of market data requests submitted to this provider.
     *
     * @return an <code>int</code> value
     */
    public int getTotalRequests();
    /**
     * Gets the number of currently active market data requests.
     *
     * @return an <code>int</code> value
     */
    public int getActiveRequests();
    /**
     * Gets the total number of events produced by this provider.
     *
     * @return an <code>int</code> value
     */
    public int getTotalEvents();
    /**
     * Gets the status of the provider.
     *
     * @return a <code>ProviderStatus</code> value
     */
    public ProviderStatus getProviderStatus();
}
