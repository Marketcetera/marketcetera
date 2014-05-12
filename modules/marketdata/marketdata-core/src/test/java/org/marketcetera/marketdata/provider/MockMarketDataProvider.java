package org.marketcetera.marketdata.provider;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.IFeedComponent.FeedType;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.core.provider.AbstractMarketDataProvider;
import org.marketcetera.marketdata.core.request.MarketDataRequestAtom;

/* $License$ */

/**
 * Provides a test market data provider.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
public class MockMarketDataProvider
        extends AbstractMarketDataProvider
{
    /**
     * Get the canceledAtoms value.
     *
     * @return a <code>List&lt;MarketDataRequestAtom&gt;</code> value
     */
    public List<MarketDataRequestAtom> getCanceledAtoms()
    {
        return canceledAtoms;
    }
    /**
     * Sets the canceledAtoms value.
     *
     * @param inCanceledAtoms a <code>List&lt;MarketDataRequestAtom&gt;</code> value
     */
    public void setCanceledAtoms(List<MarketDataRequestAtom> inCanceledAtoms)
    {
        canceledAtoms = inCanceledAtoms;
    }
    /**
     * Get the requestedAtoms value.
     *
     * @return a <code>List&lt;MarketDataRequestAtom&gt;</code> value
     */
    public List<MarketDataRequestAtom> getRequestedAtoms()
    {
        return requestedAtoms;
    }
    /**
     * Sets the requestedAtoms value.
     *
     * @param inRequestedAtoms a <code>List&lt;MarketDataRequestAtom&gt;</code> value
     */
    public void setRequestedAtoms(List<MarketDataRequestAtom> inRequestedAtoms)
    {
        requestedAtoms = inRequestedAtoms;
    }
    /**
     * Get the requests value.
     *
     * @return a <code>List&lt;MarketDataRequest&gt;</code> value
     */
    public List<MarketDataRequest> getRequests()
    {
        return requests;
    }
    /**
     * Sets the requests value.
     *
     * @param inRequests a <code>List&lt;MarketDataRequest&gt;</code> value
     */
    public void setRequests(List<MarketDataRequest> inRequests)
    {
        requests = inRequests;
    }
    /**
     * Get the started value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getStarted()
    {
        return started;
    }
    /**
     * Get the stopped value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getStopped()
    {
        return stopped;
    }
    /**
     * Sets the exceptionOnStart value.
     *
     * @param inExceptionOnStart a <code>RuntimeException</code> value
     */
    public void setExceptionOnStart(RuntimeException inExceptionOnStart)
    {
        exceptionOnStart = inExceptionOnStart;
    }
    /**
     * Sets the exceptionOnStop value.
     *
     * @param inExceptionOnStop a <code>RuntimeException</code> value
     */
    public void setExceptionOnStop(RuntimeException inExceptionOnStop)
    {
        exceptionOnStop = inExceptionOnStop;
    }
    /**
     * Sets the exceptionOnRequest value.
     *
     * @param inExceptionOnRequest a <code>RuntimeException</code> value
     */
    public void setExceptionOnRequest(RuntimeException inExceptionOnRequest)
    {
        exceptionOnRequest = inExceptionOnRequest;
    }
    /**
     * Sets the exceptionOnCancel value.
     *
     * @param inExceptionOnCancel a <code>RuntimeException</code> value
     */
    public void setExceptionOnCancel(RuntimeException inExceptionOnCancel)
    {
        exceptionOnCancel = inExceptionOnCancel;
    }
    /**
     * Sets the capabilities value.
     *
     * @param a <code>Set&lt;Capability&gt;</code> value
     */
    public void setCapabilities(Set<Capability> inCapabilities)
    {
        capabilities = inCapabilities;
    }
    /**
     * Sets the feedType value.
     *
     * @param a <code>FeedType</code> value
     */
    public void setFeedType(FeedType inFeedType)
    {
        feedType = inFeedType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.provider.MarketDataProvider#getProviderName()
     */
    @Override
    public String getProviderName()
    {
        return "mock";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.provider.MarketDataProvider#getCapabilities()
     */
    @Override
    public Set<Capability> getCapabilities()
    {
        return capabilities;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.provider.MarketDataProvider#getFeedType()
     */
    @Override
    public FeedType getFeedType()
    {
        return feedType;
    }
    /**
     * Resets the test-related mutable state.
     */
    public void reset()
    {
        canceledAtoms.clear();
        requestedAtoms.clear();
        requests.clear();
        capabilities = EnumSet.allOf(Capability.class);
        feedType = FeedType.SIMULATED;
        started = false;
        stopped = false;
        exceptionOnStart = null;
        exceptionOnStop = null;
        exceptionOnRequest = null;
        exceptionOnCancel = null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.provider.AbstractMarketDataProvider#doStart()
     */
    @Override
    protected void doStart()
    {
        started = false;
        if(exceptionOnStart != null) {
            throw exceptionOnStart;
        }
        started = true;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.provider.AbstractMarketDataProvider#doStop()
     */
    @Override
    protected void doStop()
    {
        stopped = false;
        if(exceptionOnStop != null) {
            throw exceptionOnStop;
        }
        stopped = true;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.provider.AbstractMarketDataProvider#doCancel(org.marketcetera.marketdata.request.MarketDataRequestAtom)
     */
    @Override
    protected void doCancel(MarketDataRequestAtom inAtom)
    {
        canceledAtoms.add(inAtom);
        if(exceptionOnCancel != null) {
            throw exceptionOnCancel;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.provider.AbstractMarketDataProvider#doMarketDataRequest(org.marketcetera.marketdata.request.MarketDataRequest, org.marketcetera.marketdata.request.MarketDataRequestAtom)
     */
    @Override
    protected void doMarketDataRequest(MarketDataRequest inCompleteRequest,
                                       MarketDataRequestAtom inRequestAtom)
            throws InterruptedException
    {
        requestedAtoms.add(inRequestAtom);
        requests.add(inCompleteRequest);
        if(exceptionOnRequest != null) {
            throw exceptionOnRequest;
        }
    }
    private List<MarketDataRequestAtom> canceledAtoms = new ArrayList<MarketDataRequestAtom>();
    private List<MarketDataRequestAtom> requestedAtoms = new ArrayList<MarketDataRequestAtom>();
    private List<MarketDataRequest> requests = new ArrayList<MarketDataRequest>();
    private Set<Capability> capabilities = EnumSet.allOf(Capability.class);
    private FeedType feedType = FeedType.SIMULATED;
    private boolean started = false;
    private boolean stopped = false;
    private RuntimeException exceptionOnStart;
    private RuntimeException exceptionOnStop;
    private RuntimeException exceptionOnRequest;
    private RuntimeException exceptionOnCancel;
}
