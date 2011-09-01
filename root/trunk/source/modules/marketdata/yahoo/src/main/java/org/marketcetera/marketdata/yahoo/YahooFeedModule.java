package org.marketcetera.marketdata.yahoo;

import javax.management.AttributeChangeNotification;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.AbstractMarketDataModule;
import org.marketcetera.marketdata.AbstractMarketDataModuleMXBean;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides access to Yahoo market data.
 * 
 * <p>
 * Module Features
 * <table>
 * <tr><th>Capabilities</th><td>Data Emitter</td></tr>
 * <tr><th>Stops data flows</th><td>No</td></tr>
 * <tr><th>Start Operation</th><td>Starts the feed, logs into it.</td></tr>
 * <tr><th>Stop Operation</th><td>Stops the data feed.</td></tr>
 * <tr><th>Management Interface</th><td>{@link AbstractMarketDataModuleMXBean}</td></tr>
 * <tr><th>MX Notification</th><td>{@link AttributeChangeNotification}
 * whenever {@link #getFeedStatus()} changes. </td></tr>
 * </table>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.4
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
                         Messages.MISSING_URL.getText());
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
    private volatile String url = "http://finance.yahoo.com/d/quotes.csv"; //$NON-NLS-1$
    /**
     * the interval at which to get a new quote
     */
    private volatile int refreshInterval = 250;
}
