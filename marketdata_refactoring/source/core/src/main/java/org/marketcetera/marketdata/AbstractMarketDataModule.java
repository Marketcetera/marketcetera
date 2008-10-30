package org.marketcetera.marketdata;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.AttributeChangeNotification;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.EventBase;
import org.marketcetera.module.DataEmitter;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.IllegalRequestParameterValue;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.RequestID;
import org.marketcetera.module.UnsupportedRequestParameterType;

/* $License$ */

/**
 * Base class for market data provider strategy agent modules.
 * 
 * <p>Market data providers wishing to expose a strategy agent emitter interface
 * may extend this class.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class AbstractMarketDataModule<T extends IMarketDataFeedToken<C>, 
                                               C extends IMarketDataFeedCredentials>
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
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.RequestID)
     */
    @Override
    public final void cancel(RequestID inRequestID)
    {
        synchronized(tokens) {
            T token = tokens.remove(inRequestID);
            assert(token != null);
            token.cancel();
        }
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
        org.marketcetera.marketdata.DataRequest request = null;
        if(requestPayload == null) {
            throw new IllegalRequestParameterValue(instanceURN,
                                                   null);
        }
        if(requestPayload instanceof String) {
            try {
                request = org.marketcetera.marketdata.DataRequest.newRequestFromString((String)requestPayload);
            } catch (Exception e) {
                throw new IllegalRequestParameterValue(instanceURN,
                                                       requestPayload);
            }
        } else if (requestPayload instanceof org.marketcetera.marketdata.DataRequest) {
            request = (org.marketcetera.marketdata.DataRequest)requestPayload;
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
                public void publishTo(Object inEvent)
                {
                    inSupport.send(inEvent);
                }
            };
            MarketDataFeedTokenSpec<C> spec = MarketDataFeedTokenSpec.generateTokenSpec(getCredentials(),
                                                                                        request,
                                                                                        Arrays.asList(new ISubscriber[] { subscriber }));
            synchronized(tokens) {
                tokens.put(inSupport.getRequestID(),
                           feed.execute(spec));
            }
        } catch (CoreException e) {
            inSupport.dataEmitError(e.getI18NBoundMessage(),
                                    true);
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
     * Create a new AbstractMarketDataModule instance.
     *
     * @param inInstanceURN
     * @param inFeed
     */
    protected AbstractMarketDataModule(ModuleURN inInstanceURN,
                                       IMarketDataFeed<T,C> inFeed)
    {
        super(inInstanceURN,
              false);
        instanceURN = inInstanceURN;
        feed = inFeed;
        feedStatus = feed.getFeedStatus();
        MBeanNotificationInfo notifyInfo = new MBeanNotificationInfo(new String[] { AttributeChangeNotification.ATTRIBUTE_CHANGE },
                                                                     AttributeChangeNotification.class.getName(),
                                                                     "An attribute of this MBean has changed");  // TODO message catalog
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
        feed.start();
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
     * 
     *
     *
     * @return
     * @throws CoreException 
     */
    protected abstract C getCredentials()
        throws CoreException;
    /**
     * Sets the feed status as tracked by the module.
     *
     *
     * @param inNewFeedStatus
     */
    private void setFeedStatus(FeedStatus inNewFeedStatus)
    {
        String newStatusString = inNewFeedStatus.toString();
        String oldStatusString = feedStatus.toString();
        feedStatus = inNewFeedStatus;
        mNotificationDelegate.sendNotification(new AttributeChangeNotification(this,
                                                                               mSequence.getAndIncrement(),
                                                                               System.currentTimeMillis(),
                                                                               "Feed Status Changed", // TODO message catalog
                                                                               "FeedStatus",
                                                                               "String",
                                                                               newStatusString,
                                                                               oldStatusString));
    }
    private final ModuleURN instanceURN;
    private final IMarketDataFeed<T,C> feed;
    private final Map<RequestID,T> tokens = new HashMap<RequestID,T>();
    private final NotificationBroadcasterSupport mNotificationDelegate;
    private final AtomicLong mSequence = new AtomicLong();
    private FeedStatus feedStatus;
}
