package org.marketcetera.marketdata.provider;

import java.util.Set;

import org.marketcetera.core.trade.SecurityType;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.FeedType;
import org.marketcetera.marketdata.request.MarketDataRequestToken;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataProvider
{
    public void requestMarketData(MarketDataRequestToken inRequestToken)
            throws InterruptedException;
    public void cancelMarketDataRequest(MarketDataRequestToken inRequestToken);
    public String getProviderName();
    public Set<Capability> getCapabilities();
    public Set<SecurityType> getHandledTypes();
    public FeedStatus getFeedStatus()
            throws InterruptedException;
    public FeedType getFeedType();
}
