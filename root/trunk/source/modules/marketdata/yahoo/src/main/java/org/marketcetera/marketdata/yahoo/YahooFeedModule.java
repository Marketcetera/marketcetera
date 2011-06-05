package org.marketcetera.marketdata.yahoo;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.AbstractMarketDataModule;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class YahooFeedModule
        extends AbstractMarketDataModule<YahooFeedToken,YahooFeedCredentials>
        implements YahooFeedMXBean
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.yahoo.YahooFeedMXBean#getURL()
     */
    @Override
    public String getURL()
    {
        return url;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.yahoo.YahooFeedMXBean#setURL(java.lang.String)
     */
    @Override
    public void setURL(String inURL)
    {
        url = StringUtils.trimToNull(inURL);
        Validate.notNull(url,
                         "URL must be specified");
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.yahoo.YahooFeedMXBean#getRefreshInterval()
     */
    @Override
    public String getRefreshInterval()
    {
        return Integer.toString(refreshInterval);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.yahoo.YahooFeedMXBean#setRefreshInterval(int)
     */
    @Override
    public void setRefreshInterval(String inRefreshInterval)
    {
        String rawInterval = StringUtils.trimToNull(inRefreshInterval); 
        if(rawInterval == null) {
            refreshInterval = 0;
        } else {
            refreshInterval = Integer.parseInt(rawInterval);
        }
        feed.setRefreshInterval(refreshInterval);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.yahoo.YahooFeedMXBean#getRequestCounter()
     */
    @Override
    public long getRequestCounter()
    {
        return feed.getRequestCounter();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.yahoo.YahooFeedMXBean#resetRequestCounter()
     */
    @Override
    public void resetRequestCounter()
    {
        feed.resetCounter();
    }
    /**
     * Create a new YahooFeedModule instance.
     * 
     * @throws CoreException 
     */
    YahooFeedModule()
            throws CoreException
    {
        super(YahooFeedModuleFactory.INSTANCE_URN,
              new YahooFeedFactory().getMarketDataFeed());
        feed = new YahooFeedFactory().getMarketDataFeed();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataModule#getCredentials()
     */
    @Override
    protected YahooFeedCredentials getCredentials()
            throws CoreException
    {
        return new YahooFeedCredentials(url);
    }
    /**
     * the underlying feed
     */
    private final YahooFeed feed;
    /**
     * the URL at which Yahoo provides the data
     */
    private volatile String url = "http://finance.yahoo.com/d/quotes.csv";
    /**
     * the interval at which to get a new quote
     */
    private volatile int refreshInterval = 250;
}
