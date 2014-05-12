package org.marketcetera.photon.internal.marketdata;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.ListenerList;
import org.marketcetera.core.notifications.ServerStatusListener;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.core.rpc.MarketDataRpcClientFactory;
import org.marketcetera.marketdata.core.webservice.ConnectionException;
import org.marketcetera.marketdata.core.webservice.CredentialsException;
import org.marketcetera.marketdata.core.webservice.MarketDataServiceClient;
import org.marketcetera.marketdata.core.webservice.UnknownHostException;
import org.marketcetera.marketdata.core.webservice.impl.MarketDataContextClassProvider;
import org.marketcetera.photon.core.ICredentials;
import org.marketcetera.photon.core.ICredentialsService;
import org.marketcetera.photon.core.ICredentialsService.IAuthenticationHelper;
import org.marketcetera.photon.marketdata.IFeedStatusChangedListener;
import org.marketcetera.photon.marketdata.IFeedStatusChangedListener.IFeedStatusEvent;
import org.marketcetera.photon.marketdata.IMarketData;
import org.marketcetera.photon.marketdata.IMarketDataManager;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.Inject;

/* $License$ */

/**
 * Internal implementation of {@link IMarketDataManager}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class MarketDataManager
        implements IMarketDataManager,IMarketDataClientProvider,ServerStatusListener
{
    /**
     * Create a new MarketDataManager instance.
     *
     * @param marketData
     */
    @Inject
    public MarketDataManager(final MarketData marketData)
    {
        mMarketData = marketData;
    }
    /**
     * Gets a <code>MarketDataServiceClient</code>, creating one if necessary.
     *
     * @return a <code>MarketDataServiceClient</code> value
     */
    public MarketDataServiceClient getMarketDataClient()
    {
        return marketDataClient;
    }
    @Override
    public IMarketData getMarketData()
    {
        return mMarketData;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.marketdata.IMarketDataManager#getAvailabilityCapability()
     */
    @Override
    public Set<Capability> getAvailabilityCapability()
    {
        MarketDataServiceClient client = getMarketDataClient();
        if(client == null) {
            return Collections.emptySet();
        }
        return client.getAvailableCapability();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.ServerStatusListener#receiveServerStatus(boolean)
     */
    @Override
    public void receiveServerStatus(boolean inStatus)
    {
        FeedStatusNotification update = new FeedStatusNotification(getFeedStatus(),
                                                                   FeedStatus.UNKNOWN);
        try {
            if(inStatus) {
                update.newFeedStatus = FeedStatus.AVAILABLE;
            } else {
                update.newFeedStatus = FeedStatus.ERROR;
                if(mMarketData != null) {
                    mMarketData.reset();
                }
                if(marketDataClient != null) {
                    try {
                        marketDataClient.stop();
                    } catch (Exception ignored) {}
                    marketDataClient = null;
                }
            }
        } finally {
            notifyListeners(update);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.marketdata.IMarketDataManager#setCredentialsService(org.marketcetera.photon.core.ICredentialsService)
     */
    @Override
    public void setCredentialsService(ICredentialsService inCredentialsService)
    {
        credentialsService = inCredentialsService;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.marketdata.IMarketDataManager#close()
     */
    @Override
    public void close()
    {
        if(marketDataClient != null) {
            try {
                marketDataClient.stop();
            } catch (Exception ignored) {}
            marketDataClient = null;
        }
    }
    @Override
    public void reconnectFeed()
    {
        if(!reconnecting.compareAndSet(false,
                                       true)) {
            return;
        }
        try {
            if(marketDataClient != null) {
                marketDataClient.stop();
            }
            connect();
            mMarketData.resubmit();
            if(marketDataClient != null && marketDataClient.isRunning()) {
                SLF4JLoggerProxy.info(org.marketcetera.core.Messages.USER_MSG_CATEGORY,
                                      "Market Data Nexus connection established");
            }
        } catch (ConnectionException e) {
            SLF4JLoggerProxy.error(org.marketcetera.core.Messages.USER_MSG_CATEGORY,
                                   "Cannot connect to the Market Data Nexus at {}:{}",
                                   hostname,
                                   port);
            SLF4JLoggerProxy.error(this,
                                   e,
                                   "Cannot connect to the Market Data Nexus at {}:{}",
                                   e.getHostname(),
                                   e.getPort());
        } catch (UnknownHostException e) {
            SLF4JLoggerProxy.error(org.marketcetera.core.Messages.USER_MSG_CATEGORY,
                                   "Cannot connect to the Market Data Nexus at {}:{}",
                                   hostname,
                                   port);
            SLF4JLoggerProxy.error(this,
                                   e,
                                   "Cannot connect to the Market Data Nexus at {}:{}",
                                   e.getHostname(),
                                   e.getPort());
        } catch (CredentialsException e) {
            SLF4JLoggerProxy.error(org.marketcetera.core.Messages.USER_MSG_CATEGORY,
                                   "The Market Data Nexus rejected the login attempt as {}",
                                   e.getUsername());
            SLF4JLoggerProxy.error(this,
                                   e,
                                   "The Market Data Nexus rejected the login attempt as {}",
                                   e.getUsername());
        } catch (Exception e) {
            SLF4JLoggerProxy.error(this,
                                   e);
            throw new RuntimeException(e);
        } finally {
            reconnecting.set(false);
        }
    }
    @Override
    public void addActiveFeedStatusChangedListener(
            final IFeedStatusChangedListener listener) {
        mActiveFeedListeners.add(listener);
    }

    @Override
    public void removeActiveFeedStatusChangedListener(
            final IFeedStatusChangedListener listener) {
        mActiveFeedListeners.remove(listener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.marketdata.IMarketDataManager#setHostname(java.lang.String)
     */
    @Override
    public void setHostname(String inHostname)
    {
        hostname = inHostname;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.marketdata.IMarketDataManager#setPort(int)
     */
    @Override
    public void setPort(int inPort)
    {
        port = inPort;
    }
    /**
     * Indicates if the market data connection is ready.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isRunning()
    {
        if(marketDataClient == null || !marketDataClient.isRunning()) {
            return false;
        }
        return true;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.marketdata.IMarketDataManager#isReconnecting()
     */
    @Override
    public boolean isReconnecting()
    {
        return reconnecting.get();
    }
    /**
     * Connects to the market data server, if necessary.
     */
    private void connect()
    {
        if(marketDataClient == null || !marketDataClient.isRunning()) {
            notifyListeners(new FeedStatusNotification(getFeedStatus(),
                                                       FeedStatus.UNKNOWN));
            FeedStatusNotification update = new FeedStatusNotification(getFeedStatus(),
                                                                       FeedStatus.UNKNOWN);
            try {
                boolean success = credentialsService.authenticateWithCredentials(new IAuthenticationHelper() {
                    @Override
                    public boolean authenticate(ICredentials inCredentials)
                    {
                        marketDataClient = new MarketDataRpcClientFactory().create(inCredentials.getUsername(),
                                                                                   inCredentials.getPassword(),
                                                                                   hostname,
                                                                                   port,
                                                                                   new MarketDataContextClassProvider());
                        marketDataClient.start();
                        return marketDataClient.isRunning();
                    }
                });
                update.newFeedStatus = success ? FeedStatus.AVAILABLE : FeedStatus.OFFLINE;
            } catch (ConnectionException | CredentialsException | UnknownHostException e) {
                update.newFeedStatus = FeedStatus.ERROR;
                throw e;
            } catch (RuntimeException e) {
                update.newFeedStatus = FeedStatus.ERROR;
                throw e;
            } finally {
                if(marketDataClient != null) {
                    marketDataClient.addServerStatusListener(MarketDataManager.this);
                }
                notifyListeners(update);
            }
        }
    }
    /**
     * Notifies listeners of a feed status change event.
     *
     * @param inEvent an <code>IFeedStatusEvent</code> value
     */
    private void notifyListeners(final IFeedStatusEvent inEvent)
    {
        Object[] listeners = mActiveFeedListeners.getListeners();
        for(Object object : listeners) {
            ((IFeedStatusChangedListener)object).feedStatusChanged(inEvent);
        }
    }
    @Override
    public FeedStatus getFeedStatus()
    {
        if(marketDataClient == null) {
            return FeedStatus.OFFLINE;
        }
        return marketDataClient.isRunning() ? FeedStatus.AVAILABLE : FeedStatus.ERROR;
    }
    /**
     * Manages notifications for feed status subscribers.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.0
     */
    @ClassVersion("$Id$")
    private class FeedStatusNotification
            implements IFeedStatusEvent
    {
        /**
         * Create a new FeedStatusNotification instance.
         *
         * @param inOldStatus a <code>FeedStatus</code> value
         * @param inNewStatus a <code>FeedStatus</code> value
         */
        private FeedStatusNotification(FeedStatus inOldStatus,
                                       FeedStatus inNewStatus)
        {
            oldFeedStatus = inOldStatus;
            newFeedStatus = inNewStatus;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.photon.marketdata.IFeedStatusChangedListener.IFeedStatusEvent#getSource()
         */
        @Override
        public Object getSource()
        {
            return this;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.photon.marketdata.IFeedStatusChangedListener.IFeedStatusEvent#getOldStatus()
         */
        @Override
        public FeedStatus getOldStatus()
        {
            return oldFeedStatus;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.photon.marketdata.IFeedStatusChangedListener.IFeedStatusEvent#getNewStatus()
         */
        @Override
        public FeedStatus getNewStatus()
        {
            return newFeedStatus;
        }
        /**
         * old feed status value
         */
        private FeedStatus oldFeedStatus = FeedStatus.UNKNOWN;
        /**
         * new feed status value
         */
        private FeedStatus newFeedStatus = FeedStatus.UNKNOWN;
    }
    /**
     * market data nexus hostname
     */
    private String hostname;
    /**
     * market data nexus port
     */
    private int port;
    /**
     * contains everybody who has expressed an interest in feed status
     */
    private final ListenerList mActiveFeedListeners = new ListenerList();
    /**
     * indicates if the manager is current reconnecting
     */
    private final AtomicBoolean reconnecting = new AtomicBoolean(false);
    /**
     * market data flow manager
     */
    private final MarketData mMarketData;
    /**
     * connection to the market data service
     */
    private MarketDataServiceClient marketDataClient;
    /**
     * provides access to the credentials used to connect to the server
     */
    private ICredentialsService credentialsService;
}
