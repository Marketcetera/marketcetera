package org.marketcetera.marketdata.module;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.event.Event;
import org.marketcetera.event.HasTimestamps;
import org.marketcetera.marketdata.AbstractMarketDataFeed;
import org.marketcetera.marketdata.AssetClass;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.marketdata.MarketDataFeedTokenSpec;
import org.marketcetera.marketdata.MarketDataRequest;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Provides a test market data feed implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TestFeed
        extends AbstractMarketDataFeed<TestFeedToken,
                                       TestFeedCredentials,
                                       TestFeedMessageTranslator,
                                       TestFeedEventTranslator,
                                       MarketDataRequest,
                                       TestFeed> 
{
    /**
     * Create a new TestFeed instance.
     * 
     * @throws NoMoreIDsException if the feed cannot be constructed
     */
    TestFeed()
            throws NoMoreIDsException
    {
        super(FeedType.SIMULATED,
              TestFeedModuleFactory.IDENTIFIER);
        instance = this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataFeed#getCapabilities()
     */
    @Override
    public Set<Capability> getCapabilities()
    {
        return EnumSet.allOf(Capability.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataFeed#getSupportedAssetClasses()
     */
    @Override
    public Set<AssetClass> getSupportedAssetClasses()
    {
        return EnumSet.allOf(AssetClass.class);
    }
    /**
     * Sends the given events to all market data requesters.
     *
     * @param inEvents a <code>List&lt;Event&gt;</code> value
     */
    public void sendEvents(List<Event> inEvents)
    {
        long timestamp = System.currentTimeMillis();
        for(String handle : requestsByHandle.keySet()) {
            for(Event event : inEvents) {
                if(event instanceof HasTimestamps) {
                    HasTimestamps timestampEvent = (HasTimestamps)event;
                    timestampEvent.setReceivedTimestamp(timestamp);
                }
                dataReceived(handle,
                             event);
            }
        }
    }
    /**
     * Get the requestsByToken value.
     *
     * @return a <code>Map&lt;String,MarketDataRequest&gt;</code> value
     */
    public Map<String,MarketDataRequest> getRequestsByToken()
    {
        return requestsByHandle;
    }
    /**
     * Sets an exception to throw on start.
     *
     * @param inExceptionOnStart a <code>RuntimeException</code> value or <code>null</code>
     */
    public void setExceptionOnStart(RuntimeException inExceptionOnStart)
    {
        exceptionOnStart = inExceptionOnStart;
    }
    /**
     * Causes the feed to disconnect.
     */
    public void disconnect()
    {
        logout();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#generateToken(org.marketcetera.marketdata.MarketDataFeedTokenSpec)
     */
    @Override
    protected TestFeedToken generateToken(MarketDataFeedTokenSpec inTokenSpec)
            throws FeedException
    {
        return new TestFeedToken(inTokenSpec,
                                 this);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doMarketDataRequest(java.lang.Object)
     */
    @Override
    protected List<String> doMarketDataRequest(MarketDataRequest inData)
            throws FeedException
    {
        String handle = UUID.randomUUID().toString();
        requestsByHandle.put(handle,
                             inData);
        return Lists.newArrayList(handle);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getMessageTranslator()
     */
    @Override
    protected TestFeedMessageTranslator getMessageTranslator()
    {
        return TestFeedMessageTranslator.instance;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#isLoggedIn()
     */
    @Override
    protected boolean isLoggedIn()
    {
        return isLoggedIn;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLogin(org.marketcetera.marketdata.MarketDataFeedCredentials)
     */
    @Override
    protected boolean doLogin(TestFeedCredentials inCredentials)
    {
        requestsByHandle.clear();
        if(exceptionOnStart != null) {
            throw exceptionOnStart;
        }
        isLoggedIn = true;
        return true;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLogout()
     */
    @Override
    protected void doLogout()
    {
        isLoggedIn = false;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doCancel(java.lang.String)
     */
    @Override
    protected void doCancel(String inHandle)
    {
        requestsByHandle.remove(inHandle);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getEventTranslator()
     */
    @Override
    protected TestFeedEventTranslator getEventTranslator()
    {
        return TestFeedEventTranslator.instance;
    }
    /**
     * singleton instance of this object
     */
    public static volatile TestFeed instance;
    /**
     * indicates if the feed is logged in or not
     */
    private volatile boolean isLoggedIn = false;
    /**
     * holds requests by feed-assigned handle
     */
    private final Map<String,MarketDataRequest> requestsByHandle = new HashMap<>();
    private RuntimeException exceptionOnStart = null;
}
