package org.marketcetera.marketdata.core.request;

import java.io.Serializable;

import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Used to communicate with a market data provider for a market data request.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public interface MarketDataRequestToken
        extends Serializable
{
    /**
     * Gets the id value.
     *
     * @return a <code>long</code> value
     */
    public long getId();
    /**
     * Gets the subscriber to which the market data will be delivered.
     *
     * @return an <code>ISubscriber</code> value or <code>null</code> indicating no updates are to be sent
     */
    public ISubscriber getSubscriber();
    /**
     * Gets the market data request.
     *
     * @return a <code>MarketDataRequest</code> value
     */
    public MarketDataRequest getRequest();
}
