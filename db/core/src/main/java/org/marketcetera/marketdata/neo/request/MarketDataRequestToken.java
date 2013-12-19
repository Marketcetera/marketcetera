package org.marketcetera.marketdata.neo.request;

import java.io.Serializable;

import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents a specific market data request.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataRequestToken.java 16344 2012-11-01 20:24:40Z colin $
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface MarketDataRequestToken
        extends Serializable
{
    /**
     * Gets the request id.
     *
     * @return a <code>long</code> value
     */
    public long getId();
    /**
     * Gets the subscriber for the request.
     *
     * @return an <code>ISubscriber</code> value
     */
    public ISubscriber getSubscriber();
    /**
     * Gets the request.
     *
     * @return a <code>MarketDataRequest</code> value
     */
    public MarketDataRequest getRequest();
}
