package com.marketcetera.marketdata.reuters;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.marketcetera.marketdata.*;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides access to Reuters data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ReutersFeed.java 82351 2012-05-04 21:46:58Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: ReutersFeed.java 82351 2012-05-04 21:46:58Z colin $")
public class ReutersFeed
        extends AbstractMarketDataFeed<ReutersFeedToken,
                                       ReutersFeedCredentials,
                                       ReutersFeedMessageTranslator,
                                       ReutersFeedEventTranslator,
                                       List<ReutersRequest>,
                                       ReutersFeed>
{
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "Reuters Feed";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#start()
     */
    @Override
    public void start()
    {
        if(client == null) {
            setFeedStatus(FeedStatus.ERROR);
            throw new IllegalArgumentException("No client supplied to the feed, cannot connect");
        }
        super.start();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataFeed#getCapabilities()
     */
    @Override
    public Set<Capability> getCapabilities()
    {
        return CAPABILITIES;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataFeed#getSupportedAssetClasses()
     */
    @Override
    public Set<AssetClass> getSupportedAssetClasses()
    {
        return ASSET_CLASSES;
    }
    /**
     * Create a new ReutersFeed instance.
     */
    ReutersFeed()
    {
        super(FeedType.LIVE,
              Messages.PROVIDER_DESCRIPTION.getText());
        eventTranslator = new ReutersFeedEventTranslator();
        messageTranslator = new ReutersFeedMessageTranslator();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#generateToken(org.marketcetera.marketdata.MarketDataFeedTokenSpec)
     */
    @Override
    protected ReutersFeedToken generateToken(MarketDataFeedTokenSpec inTokenSpec)
    {
        return new ReutersFeedToken(inTokenSpec,
                                    this);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doMarketDataRequest(java.util.list)
     */
    @Override
    protected List<String> doMarketDataRequest(List<ReutersRequest> inData)
    {
        return client.doMarketDataRequest(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getMessageTranslator()
     */
    @Override
    protected ReutersFeedMessageTranslator getMessageTranslator()
    {
        return messageTranslator;
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
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLogin(org.marketcetera.marketdata.MarketDataFeedCredentials)
     */
    @Override
    protected boolean doLogin(ReutersFeedCredentials inCredentials)
    {
        return client.doLogin(inCredentials);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLogout()
     */
    @Override
    protected void doLogout()
    {
        client.doLogout();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doCancel(java.lang.String)
     */
    @Override
    protected void doCancel(String inHandle)
    {
        client.doCancel(inHandle);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getEventTranslator()
     */
    @Override
    protected ReutersFeedEventTranslator getEventTranslator()
    {
        return eventTranslator;
    }
    /**
     * Sets the client value.
     *
     * @param a <code>MarketdataClient&lt;List&lt;ReutersRequest&gt;,ReutersFeedCredentials&gt;</code> value
     */
    void setClient(MarketdataClient<List<ReutersRequest>,ReutersFeedCredentials> inClient)
    {
        client = inClient;
    }
    /**
     * market data client responsible for connections to the reuters server
     */
    private volatile MarketdataClient<List<ReutersRequest>,ReutersFeedCredentials> client;
    /**
     * translates market data requests to reuters-specific types
     */
    private final ReutersFeedMessageTranslator messageTranslator;
    /**
     * translates reuters-specific data to events
     */
    private final ReutersFeedEventTranslator eventTranslator;
    /**
     * indicates the capabilities of this adapter
     */
    private final Set<Capability> CAPABILITIES = new HashSet<Capability>();
    /**
     * indicates the asset classes supported by this adapter
     */
    private final Set<AssetClass> ASSET_CLASSES = new HashSet<AssetClass>();
}
