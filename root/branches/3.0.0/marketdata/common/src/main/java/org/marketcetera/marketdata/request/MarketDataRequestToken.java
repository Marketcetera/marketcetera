package org.marketcetera.marketdata.request;

import java.io.Serializable;

import org.marketcetera.api.systemmodel.Subscriber;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataRequestToken
        extends Serializable
{
    public long getId();
    public Subscriber getSubscriber();
    public MarketDataRequest getRequest();
}
