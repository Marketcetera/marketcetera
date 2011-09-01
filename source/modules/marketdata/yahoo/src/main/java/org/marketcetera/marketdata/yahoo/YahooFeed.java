package org.marketcetera.marketdata.yahoo;

import static org.marketcetera.marketdata.AssetClass.EQUITY;
import static org.marketcetera.marketdata.AssetClass.FUTURE;
import static org.marketcetera.marketdata.AssetClass.OPTION;
import static org.marketcetera.marketdata.Capability.EVENT_BOUNDARY;
import static org.marketcetera.marketdata.Capability.LATEST_TICK;
import static org.marketcetera.marketdata.Capability.MARKET_STAT;
import static org.marketcetera.marketdata.Capability.TOP_OF_BOOK;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.GuardedBy;

import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.marketdata.*;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Market data feed implementation for the Yahoo market data supplier.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
class YahooFeed
        extends AbstractMarketDataFeed<YahooFeedToken,
                                       YahooFeedCredentials,
                                       YahooFeedMessageTranslator,
                                       YahooFeedEventTranslator,
                                       List<YahooRequest>,
                                       YahooFeed>
        implements YahooFeedServices
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataFeed#getCapabilities()
     */
    @Override
    public Set<Capability> getCapabilities()
    {
        return capabilities;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataFeed#getSupportedAssetClasses()
     */
    @Override
    public Set<AssetClass> getSupportedAssetClasses()
    {
        return assetClasses;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.yahoo.FeedServices#doDataReceived(java.lang.String, java.lang.Object)
     */
    @Override
    public void doDataReceived(String inHandle,
                               Object inData)
    {
        dataReceived(inHandle,
                     inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.yahoo.FeedServices#getRefreshInterval()
     */
    @Override
    public int getRefreshInterval()
    {
        return refreshInterval;
    }
    /**
     * Resets the request counter.
     */
    void resetCounter()
    {
        client.resetRequestcounter();
    }
    /**
     * Gets the current request counter. 
     *
     * @return a <code>long</code> value
     */
    long getRequestCounter()
    {
        return client.getRequestCounter();
    }
    /**
     * Create a new YahooFeed instance.
     *
     * @param inProviderName a <code>String</code> value
     * @throws NoMoreIDsException if the feed cannot be constructed
     */
    YahooFeed(String inProviderName,
              YahooClientFactory inFactory)
            throws NoMoreIDsException
    {
        super(FeedType.DELAYED,
              inProviderName);
        client = inFactory.getClient(this);
    }
    /**
     * Sets the refresh interval for retrieving market data.
     * 
     * <p>Changing this value takes effect immediately.
     *
     * @param inRefreshInterval an <code>int</code> value
     */
    void setRefreshInterval(int inRefreshInterval)
    {
        refreshInterval = inRefreshInterval;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doCancel(java.lang.String)
     */
    @Override
    protected void doCancel(String inHandle)
    {
        synchronized(requests) {
            YahooRequest request = requests.remove(inHandle);
            if(request != null) {
                client.cancel(request);
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLogin(org.marketcetera.marketdata.MarketDataFeedCredentials)
     */
    @Override
    protected boolean doLogin(YahooFeedCredentials inCredentials)
    {
        return client.login(inCredentials);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLogout()
     */
    @Override
    protected void doLogout()
    {
        client.logout();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#isLoggedIn()
     */
    @Override
    protected boolean isLoggedIn()
    {
        return client.isLoggedIn();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doMarketDataRequest(java.lang.Object)
     */
    @Override
    protected List<String> doMarketDataRequest(List<YahooRequest> inRequests)
            throws FeedException
    {
        List<String> handles = new ArrayList<String>();
        synchronized(requests) {
            for(YahooRequest request : inRequests) {
                String handle = generateHandle();
                handles.add(handle);
                request.setHandle(handle);
                requests.put(handle,
                             request);
                client.request(request);
            }
            return handles;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#generateToken(org.marketcetera.marketdata.MarketDataFeedTokenSpec)
     */
    @Override
    protected YahooFeedToken generateToken(MarketDataFeedTokenSpec inTokenSpec)
            throws FeedException
    {
        return new YahooFeedToken(inTokenSpec,
                                  this);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getEventTranslator()
     */
    @Override
    protected YahooFeedEventTranslator getEventTranslator()
    {
        return YahooFeedEventTranslator.INSTANCE;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getMessageTranslator()
     */
    @Override
    protected YahooFeedMessageTranslator getMessageTranslator()
    {
        return YahooFeedMessageTranslator.INSTANCE;
    }
    /**
     * Generates a request handle.
     *
     * @return a <code>String</code> value
     */
    private String generateHandle()
    {
        return String.format("yahoo-%s", //$NON-NLS-1$
                             counter.incrementAndGet());
    }
    /**
     * default interval at which to refresh the market data
     */
    private volatile int refreshInterval = 250;
    /**
     * asset classes supported by this adapter
     */
    private static final Set<AssetClass> assetClasses = new HashSet<AssetClass>(Arrays.asList(new AssetClass[] { EQUITY,OPTION,FUTURE }));
    /**
     * capabilities of this adapter
     */
    private static final Set<Capability> capabilities = new HashSet<Capability>(Arrays.asList(new Capability[] { TOP_OF_BOOK,LATEST_TICK,MARKET_STAT,EVENT_BOUNDARY }));
    /**
     * stores the active requests
     */
    @GuardedBy("requests")
    private final Map<String,YahooRequest> requests = new HashMap<String,YahooRequest>();
    /**
     * counter used to count requests
     */
    private final AtomicLong counter = new AtomicLong(0);
    /**
     * client implementation to use
     */
    private final YahooClient client;
}
