package org.marketcetera.marketdata;

import static org.marketcetera.marketdata.Messages.BEAN_ATTRIBUTE_CHANGED;
import static org.marketcetera.marketdata.Messages.FEED_STATUS_CHANGED;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.AttributeChangeNotification;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

import org.marketcetera.core.CoreException;
import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.core.LockHelper;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.EventBase;
import org.marketcetera.module.DataEmitter;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.IllegalRequestParameterValue;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.RequestID;
import org.marketcetera.module.UnsupportedRequestParameterType;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/* $License$ */

/**
 * Base class for market data provider strategy agent modules.
 * 
 * <p>Market data providers wishing to expose a strategy agent emitter interface
 * may extend this class.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class AbstractMarketDataModule<T extends MarketDataFeedToken, 
                                               C extends MarketDataFeedCredentials>
        extends Module
        implements DataEmitter, AbstractMarketDataModuleMXBean, NotificationEmitter
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataModuleMXBean#getFeedStatus()
     */
    @Override
    public final String getFeedStatus()
    {
        if(feedStatus == null) {
            return FeedStatus.OFFLINE.toString();
        }
        return feedStatus.toString();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataModuleMXBean#reconnect()
     */
    @Override
    public void reconnect()
    {
        try {
            feed.logout();
            feed.login(getCredentials());
        } catch (Exception e) {
            SLF4JLoggerProxy.error(this,
                                   e);
            throw new IllegalArgumentException(e);
        }
        if(feed instanceof AbstractMarketDataFeed) {
            ((AbstractMarketDataFeed<?,?,?,?,?,?>)feed).doReconnectToFeed();
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.RequestID)
     */
    @Override
    public final void cancel(DataFlowID inFlowID,
                             final RequestID inRequestID)
    {
        requestLock.executeWrite(new Runnable() {
            @Override
            public void run()
            {
                T token = requests.inverse().remove(inRequestID);
                if(token != null) {
                    token.cancel();
                }
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#requestData(org.marketcetera.module.DataRequest, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    public final void requestData(DataRequest inRequest,
                                  final DataEmitterSupport inSupport)
            throws UnsupportedRequestParameterType, IllegalRequestParameterValue
    {
        Object requestPayload = inRequest.getData();
        if(requestPayload == null) {
            throw new IllegalRequestParameterValue(instanceURN,
                                                   null);
        }
        MarketDataRequest request = null;
        if(requestPayload instanceof String) {
            try {
                request = MarketDataRequest.newRequestFromString((String)requestPayload);
            } catch (Exception e) {
                throw new IllegalRequestParameterValue(instanceURN,
                                                       requestPayload,
                                                       e);
            }
        } else if (requestPayload instanceof MarketDataRequest) {
            request = (MarketDataRequest)requestPayload;
        } else {
            throw new UnsupportedRequestParameterType(instanceURN,
                                                      requestPayload);
        }
        try {
            ISubscriber subscriber = new ISubscriber() {
                @Override
                public boolean isInteresting(Object inData)
                {
                    return inData instanceof EventBase;
                }
                @Override
                public void publishTo(final Object inEvent)
                {
                    if(inEvent instanceof EventBase) {
                        requestLock.executeRead(new Runnable() {
                            @Override
                            public void run()
                            {
                                EventBase event = (EventBase)inEvent;
                                Object token = event.getSource();
                                RequestID requestID = requests.get(token);
                                event.setSource(requestID);
                            }
                        });
                    }
                    inSupport.send(inEvent);
                }
            };
            MarketDataFeedTokenSpec spec = MarketDataFeedTokenSpec.generateTokenSpec(request,
                                                                                     subscriber);
            final T token = feed.execute(spec);
            requestLock.executeWrite(new Runnable() {
                @Override
                public void run()
                {
                    requests.put(token,
                                 inSupport.getRequestID());
                }
            });
        } catch (Exception e) {
            throw new IllegalRequestParameterValue(instanceURN,
                                                   requestPayload,
                                                   e);
        }
    }
    /* (non-Javadoc)
     * @see javax.management.NotificationEmitter#removeNotificationListener(javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
     */
    @Override
    public final void removeNotificationListener(NotificationListener inListener,
                                                 NotificationFilter inFilter,
                                                 Object inHandback)
            throws ListenerNotFoundException
    {
        mNotificationDelegate.removeNotificationListener(inListener,
                                                         inFilter,
                                                         inHandback);
    }
    /* (non-Javadoc)
     * @see javax.management.NotificationBroadcaster#addNotificationListener(javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
     */
    @Override
    public final void addNotificationListener(NotificationListener inListener,
                                              NotificationFilter inFilter,
                                              Object inHandback)
            throws IllegalArgumentException
    {
        mNotificationDelegate.addNotificationListener(inListener,
                                                      inFilter,
                                                      inHandback);
    }
    /* (non-Javadoc)
     * @see javax.management.NotificationBroadcaster#getNotificationInfo()
     */
    @Override
    public final MBeanNotificationInfo[] getNotificationInfo()
    {
        return mNotificationDelegate.getNotificationInfo();
    }
    /* (non-Javadoc)
     * @see javax.management.NotificationBroadcaster#removeNotificationListener(javax.management.NotificationListener)
     */
    @Override
    public final void removeNotificationListener(NotificationListener inListener)
            throws ListenerNotFoundException
    {
        mNotificationDelegate.removeNotificationListener(inListener);
    }
    /**
     * used to indicate unknown capabilities of a provider
     */
    private static final Set<Capability> unknownCapabilities = EnumSet.of(Capability.UNKNOWN);
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataModuleMXBean#getCapabilities()
     */
    @Override
    public Set<Capability> getCapabilities()
    {
        Set<Capability> capabilities;
        if(feed == null ||
           (capabilities = feed.getCapabilities()) == null) {
            return unknownCapabilities;
        }
        return capabilities;
    }
    /**
     * Create a new AbstractMarketDataModule instance.
     *
     * @param inInstanceURN a <code>ModuleURN</code> value containing the URN of the module
     * @param inFeed an <code>IMarketDataFeed&lt;T,C&gt;</code> value containing the instance of the market data feed that this module wraps
     */
    protected AbstractMarketDataModule(ModuleURN inInstanceURN,
                                       MarketDataFeed<T,C> inFeed)
    {
        super(inInstanceURN,
              false);
        instanceURN = inInstanceURN;
        feed = inFeed;
        feedStatus = feed.getFeedStatus();
        MBeanNotificationInfo notifyInfo = new MBeanNotificationInfo(new String[] { AttributeChangeNotification.ATTRIBUTE_CHANGE },
                                                                     AttributeChangeNotification.class.getName(),
                                                                     BEAN_ATTRIBUTE_CHANGED.getText());
        mNotificationDelegate = new NotificationBroadcasterSupport(notifyInfo);
        feed.addFeedComponentListener(new IFeedComponentListener() {
            @Override
            public void feedComponentChanged(IFeedComponent inComponent)
            {
                setFeedStatus(inComponent.getFeedStatus());
            }
        });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
        SLF4JLoggerProxy.debug(this,
                               "starting {}", //$NON-NLS-1$
                               this);
        feed.start();
        try {
            feed.login(getCredentials());
        } catch (Exception e) {
            throw new ModuleException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
        feed.stop();
    }
    /**
     * Returns a credentials instance relevant to this feed.
     *
     * @return a <code>C</code> value
     * @throws CoreException if the credentials object cannot be created
     */
    protected abstract C getCredentials()
        throws CoreException;
    /**
     * Sets the feed status as tracked by the module.
     *
     * @param inNewFeedStatus a <code>FeedStatus</code> value
     */
    private void setFeedStatus(FeedStatus inNewFeedStatus)
    {
        String newStatusString = inNewFeedStatus.toString();
        String oldStatusString = feedStatus.toString();
        feedStatus = inNewFeedStatus;
        mNotificationDelegate.sendNotification(new AttributeChangeNotification(this,
                                                                               mSequence.getAndIncrement(),
                                                                               System.currentTimeMillis(),
                                                                               FEED_STATUS_CHANGED.getText(),
                                                                               "FeedStatus", //$NON-NLS-1$
                                                                               "String", //$NON-NLS-1$
                                                                               oldStatusString,
                                                                               newStatusString));
    }
    /**
     * this is the unique instance URN of the module
     */
    private final ModuleURN instanceURN;
    /**
     * the actual feed object that handles market data requests
     */
    private final MarketDataFeed<T,C> feed;
    /**
     * tracks the tokens of active data requests
     */
    private final BiMap<T,RequestID> requests = HashBiMap.create();
    /**
     * lock used for {@link #requests} access
     */
    private final LockHelper requestLock = new LockHelper();
    /**
     * provides JMX notification services
     */
    private final NotificationBroadcasterSupport mNotificationDelegate;
    /**
     * counter providing increasing sequence of values unique to this JVM instance 
     */
    private final AtomicLong mSequence = new AtomicLong();
    /**
     * the feed status of the underlying feed object 
     */
    private FeedStatus feedStatus;
}
