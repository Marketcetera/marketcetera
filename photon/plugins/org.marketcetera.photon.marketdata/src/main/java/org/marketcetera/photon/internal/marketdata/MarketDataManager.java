package org.marketcetera.photon.internal.marketdata;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.AttributeChangeNotification;
import javax.management.NotificationEmitter;

import org.eclipse.core.runtime.ListenerList;
import org.marketcetera.core.notifications.ServerStatusListener;
import org.marketcetera.marketdata.AbstractMarketDataModule;
import org.marketcetera.marketdata.AbstractMarketDataModuleMXBean;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.core.webservice.ConnectionException;
import org.marketcetera.marketdata.core.webservice.CredentialsException;
import org.marketcetera.marketdata.core.webservice.MarketDataServiceClient;
import org.marketcetera.marketdata.core.webservice.impl.MarketDataContextClassProvider;
import org.marketcetera.marketdata.core.webservice.impl.MarketDataServiceClientFactoryImpl;
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
 * <h4>Market Data Module Abstraction</h4>
 * 
 * This class discovers available market data modules that follow the following
 * conventions:
 * <ol>
 * <li>Have a module provider type of "mdata"</li>
 * <li>Are singleton modules</li>
 * <li>Implement the {@link AbstractMarketDataModuleMXBean} interface</li>
 * <li>Implement the {@link NotificationEmitter} interface</li>
 * </ol>
 * Modules that don't adhere to these conventions will not be supported.
 * Additionally, although it is not validated, the module must send
 * {@link AttributeChangeNotification attribute change notifications} for the
 * "FeedStatus" attribute when the feed's status changes in order for this class
 * to function properly. Typically, all market data modules will extend
 * {@link AbstractMarketDataModule}, which provides much of the needed
 * functionality.
 * <p>
 * Each market data module is proxied by a {@link MarketDataFeed} instance that
 * handles the interactions with the underlying modules.
 * 
 * <h4>Active Feed Management</h4>
 * 
 * The current market data UI paradigm associates all market data display with a
 * single feed. This class supports this model by maintaining an active feed and
 * delegating all requests to that feed. It also has an internal
 * {@link IFeedStatusChangedListener} that listens to feed status changes on all
 * its feeds and directs notifications from the active feed to listeners
 * registered with this class.
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
        if(!mReconnecting.compareAndSet(false,
                                        true)) {
            return;
        }
        try {
            if(marketDataClient != null) {
                marketDataClient.stop();
                marketDataClient = null;
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
            mReconnecting.set(false);
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
                        marketDataClient = new MarketDataServiceClientFactoryImpl().create(inCredentials.getUsername(),
                                                                                           inCredentials.getPassword(),
                                                                                           hostname,
                                                                                           port,
                                                                                           new MarketDataContextClassProvider());
                        marketDataClient.addServerStatusListener(MarketDataManager.this);
                        marketDataClient.start();
                        return marketDataClient.isRunning();
                    }
                });
                update.newFeedStatus = success ? FeedStatus.AVAILABLE : FeedStatus.OFFLINE;
            } catch (ConnectionException | CredentialsException e) {
                update.newFeedStatus = FeedStatus.ERROR;
                throw e;
            } catch (RuntimeException e) {
                update.newFeedStatus = FeedStatus.ERROR;
                throw e;
            } finally {
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
     * @since $Release$
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
     * 
     */
    private String hostname;
    /**
     * 
     */
    private int port;
    /**
     * 
     */
    private final ListenerList mActiveFeedListeners = new ListenerList();
    /**
     * 
     */
    private final AtomicBoolean mReconnecting = new AtomicBoolean(false);
    /**
     * 
     */
    private final MarketData mMarketData;
    /**
     * 
     */
    private MarketDataServiceClient marketDataClient;
    /**
     * 
     */
    private ICredentialsService credentialsService;
}
