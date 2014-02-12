package org.marketcetera.marketdata.core.request;

import java.io.Serializable;

import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.marketdata.MarketDataRequest;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataRequestToken.java 16344 2012-11-01 20:24:40Z colin $
 * @since $Release$
 */
public interface MarketDataRequestToken
        extends Serializable
{
    public long getId();
    public ISubscriber getSubscriber();
    public MarketDataRequest getRequest();
}
