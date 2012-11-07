package org.marketcetera.marketdata.provider;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.core.trade.Instrument;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.events.Event;
import org.marketcetera.marketdata.manager.MarketDataProviderNotAvailable;
import org.marketcetera.marketdata.manager.MarketDataProviderRegistry;
import org.marketcetera.marketdata.manager.MarketDataRequestFailed;
import org.marketcetera.marketdata.request.MarketDataRequest;
import org.marketcetera.marketdata.request.MarketDataRequestAtom;
import org.marketcetera.marketdata.request.MarketDataRequestToken;
import org.springframework.context.Lifecycle;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 * TODO need to handle restart case such that existing subscriptions are honored (resubmit and replace internal handles)
 * TODO implement JMX interface
 */
@ThreadSafe
public abstract class AbstractMarketDataProvider
        implements MarketDataProvider, Lifecycle
{
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public synchronized void start()
    {
        if(isRunning()) {
            stop();
        }
        keepAlive.set(true);
        notifier = new EventNotifier();
        notifier.start();
        doStart();
        // the next bit of code is executed only if the delegated start function succeeds - exceptions are intentionally not caught
        running.set(true);
        try {
            setFeedStatus(FeedStatus.AVAILABLE);
        } catch (InterruptedException e) {
            throw new RuntimeException(e); // TODO change to FailedToStartException
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public synchronized void stop()
    {
        if(!isRunning()) {
            return;
        }
        try {
            keepAlive.set(false);
            doStop();
            notifier.stop();
        } finally {
            running.set(false);
            try {
                if(getFeedStatus() != FeedStatus.ERROR) {
                    setFeedStatus(FeedStatus.OFFLINE);
                }
            } catch (InterruptedException ignored) {}
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running.get();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.provider.MarketDataProvider#requestMarketData(org.marketcetera.marketdata.request.MarketDataRequestToken, org.marketcetera.api.systemmodel.Subscriber)
     */
    @Override
    public void requestMarketData(MarketDataRequestToken inRequestToken)
            throws InterruptedException
    {
        if(!isRunning()) {
            throw new MarketDataProviderNotAvailable();
        }
        Set<MarketDataRequestAtom> atoms = explodeRequest(inRequestToken.getRequest());
        Set<String> internalHandles = new HashSet<String>();
        AtomicBoolean updateSemaphore = new AtomicBoolean(false);
        // TODO examine request atoms and make sure they can all be processed by the given provider (check capabilities and handled security types)
        try {
            for(MarketDataRequestAtom atom : atoms) {
                internalHandles.add(doMarketDataRequest(inRequestToken.getRequest(),
                                                        atom,
                                                        updateSemaphore));
            }
        } catch (Exception e) {
            for(String internalHandle : internalHandles) {
                try {
                    doCancel(internalHandle);
                } catch (Exception ignored) {}
            }
            if(e instanceof InterruptedException) {
                throw (InterruptedException)e;
            }
            throw new MarketDataRequestFailed(); // TODO add reason
        }
        for(String internalHandle : internalHandles) {
            handleMap.put(internalHandle,
                          inRequestToken);
            
        }
        // TODO locking
        internalHandlesByToken.putAll(inRequestToken,
                                      internalHandles);
        updateSemaphore.set(true);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.provider.MarketDataProvider#cancelMarketDataRequest(org.marketcetera.marketdata.request.MarketDataRequestToken)
     */
    @Override
    public void cancelMarketDataRequest(MarketDataRequestToken inRequestToken)
    {
        // TODO locking
        for(String internalHandle : internalHandlesByToken.removeAll(inRequestToken)) {
            doCancel(internalHandle);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataProvider#getFeedStatus()
     */
    @Override
    public FeedStatus getFeedStatus()
            throws InterruptedException
    {
        Lock getFeedStatusLock = statusLock.readLock();
        try {
            getFeedStatusLock.lockInterruptibly();
            return status;
        } finally {
            getFeedStatusLock.unlock();
        }
    }
    /**
     * Get the providerRegistry value.
     *
     * @return a <code>MarketDataProviderRegistry</code> value
     */
    public MarketDataProviderRegistry getProviderRegistry()
    {
        return providerRegistry;
    }
    /**
     * Sets the providerRegistry value.
     *
     * @param inProviderRegistry a <code>MarketDataProviderRegistry</code> value
     */
    public void setProviderRegistry(MarketDataProviderRegistry inProviderRegistry)
    {
        providerRegistry = inProviderRegistry;
    }
    /**
     * 
     *
     *
     * @param inInternalHandles
     * @param inEventData
     */
    protected void dataReceived(Set<String> inInternalHandles,
                                Event inEventData)
    {
        for(String handle : inInternalHandles) {
            dataReceived(handle,
                         inEventData);
        }
    }
    protected void dataReceived(Set<String> inInternalHandles,
                                List<Event> inEvents)
    {
        for(String handle : inInternalHandles) {
            dataReceived(handle,
                         inEvents);
        }
    }
    protected void dataReceived(String inInternalHandle,
                                List<Event> inEvents)
    {
        for(Event event : inEvents) {
            dataReceived(inInternalHandle,
                         event);
        }
    }
    /**
     * 
     *
     *
     * @param inInternalHandle
     * @param inEvent
     */
    protected void dataReceived(String inInternalHandle,
                                Event inEvent)
    {
        notifications.add(new EventNotification(inEvent,
                                                inInternalHandle));
    }
    private class EventNotifier
            implements Runnable, Lifecycle
    {
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            try {
                while(keepAlive.get()) {
                    running.set(true);
                    EventNotification notification = notifications.take();
                    MarketDataRequestToken token = handleMap.get(notification.handle);
                    try {
                        token.getSubscriber().publishTo(notification.event);
                    } catch (Exception e) {
                        // TODO log message
                    }
                }
            } catch (InterruptedException e) {
                
            } finally {
                running.set(false);
            }
        }
        /* (non-Javadoc)
         * @see org.springframework.context.Lifecycle#start()
         */
        @Override
        public synchronized void start()
        {
            if(running.get()) {
                return;
            }
            keepAlive.set(true);
            thread = new Thread(this,
                                "Market data notifier thread for " + getProviderName());
            thread.start();
        }
        /* (non-Javadoc)
         * @see org.springframework.context.Lifecycle#stop()
         */
        @Override
        public synchronized void stop()
        {
            if(!running.get()) {
                return;
            }
            keepAlive.set(false);
            if(thread != null) {
                thread.interrupt();
                try {
                    thread.join();
                } catch (InterruptedException ignored) {}
                thread = null;
            }
        }
        /* (non-Javadoc)
         * @see org.springframework.context.Lifecycle#isRunning()
         */
        @Override
        public boolean isRunning()
        {
            return running.get();
        }
        private final AtomicBoolean keepAlive = new AtomicBoolean(false);
        private final AtomicBoolean running = new AtomicBoolean(false);
        private volatile Thread thread;
    }
    private static class EventNotification
    {
        private EventNotification(Event inEvent,
                                  String inHandle)
        {
            event = inEvent;
            handle = inHandle;
        }
        private final Event event;
        private final String handle;
    }
    /**
     * 
     *
     *
     * @param inNewStatus
     * @throws InterruptedException 
     */
    protected void setFeedStatus(FeedStatus inNewStatus)
            throws InterruptedException
    {
        Lock setFeedStatusLock = statusLock.writeLock();
        boolean shouldNotify = false;
        try {
            setFeedStatusLock.lockInterruptibly();
            if(inNewStatus != status) {
                shouldNotify = true;
                status = inNewStatus;
            }
        } finally {
            setFeedStatusLock.unlock();
        }
        // this statement is intentionally outside the lock and does not use the member variable
        //  to avoid something odd happening with the publisher that causes the status lock to be held
        //  for an unknown amount of time
        if(shouldNotify) {
            if(providerRegistry != null) {
                providerRegistry.setStatus(this,
                                           inNewStatus);
            }
        }
    }
    /**
     * 
     *
     *
     */
    protected abstract void doStart();
    /**
     * 
     *
     *
     */
    protected abstract void doStop();
    /**
     * 
     *
     *
     * @param inInternalHandle
     */
    protected abstract void doCancel(String inInternalHandle);
    /**
     * 
     *
     *
     * @param inCompleteRequest
     * @param inRequestAtom
     * @param inUpdateSemaphore
     * @return
     * @throws InterruptedException
     */
    protected abstract String doMarketDataRequest(MarketDataRequest inCompleteRequest,
                                                  MarketDataRequestAtom inRequestAtom,
                                                  AtomicBoolean inUpdateSemaphore)
            throws InterruptedException;
    /**
     * 
     *
     *
     * @param inRequest
     * @return
     */
    private Set<MarketDataRequestAtom> explodeRequest(MarketDataRequest inRequest)
    {
        Set<MarketDataRequestAtom> atoms = new HashSet<MarketDataRequestAtom>();
        if(inRequest.getInstruments().isEmpty()) {
            for(Instrument underlyingInstrument : inRequest.getUnderlyingInstruments()) {
                for(Content content : inRequest.getContent()) {
                    atoms.add(new MarketDataRequestAtomImpl(null,
                                                            underlyingInstrument,
                                                            content));
                }
            }
        } else {
            for(Instrument instrument : inRequest.getInstruments()) {
                for(Content content : inRequest.getContent()) {
                    atoms.add(new MarketDataRequestAtomImpl(instrument,
                                                            null,
                                                            content));
                }
            }
        }
        return atoms;
    }
    /**
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @Immutable
    private static class MarketDataRequestAtomImpl
            implements MarketDataRequestAtom
    {
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.provider.MarketDataRequestAtom#getInstrument()
         */
        @Override
        public Instrument getInstrument()
        {
            return instrument;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.provider.MarketDataRequestAtom#getContent()
         */
        @Override
        public Content getContent()
        {
            return content;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.provider.MarketDataRequestAtom#getUnderlyingInstrument()
         */
        @Override
        public Instrument getUnderlyingInstrument()
        {
            return underlyingInstrument;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append(content).append(" : ").append(instrument == null ? underlyingInstrument : instrument);
            return builder.toString();
        }
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((content == null) ? 0 : content.hashCode());
            result = prime * result + ((instrument == null) ? 0 : instrument.hashCode());
            result = prime * result + ((underlyingInstrument == null) ? 0 : underlyingInstrument.hashCode());
            return result;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof MarketDataRequestAtomImpl)) {
                return false;
            }
            MarketDataRequestAtomImpl other = (MarketDataRequestAtomImpl) obj;
            if (content != other.content) {
                return false;
            }
            if (instrument == null) {
                if (other.instrument != null) {
                    return false;
                }
            } else if (!instrument.equals(other.instrument)) {
                return false;
            }
            if (underlyingInstrument == null) {
                if (other.underlyingInstrument != null) {
                    return false;
                }
            } else if (!underlyingInstrument.equals(other.underlyingInstrument)) {
                return false;
            }
            return true;
        }
        /**
         * Create a new MarketDataRequestAtomImpl instance.
         *
         * @param inInstrument
         * @param inUnderlyingInstrument
         * @param inContent
         */
        private MarketDataRequestAtomImpl(Instrument inInstrument,
                                          Instrument inUnderlyingInstrument,
                                          Content inContent)
        {
            instrument = inInstrument;
            underlyingInstrument = inUnderlyingInstrument;
            content = inContent;
        }
        private final Instrument underlyingInstrument;
        private final Instrument instrument;
        private final Content content;
    }
    private final ReadWriteLock statusLock = new ReentrantReadWriteLock();
    @GuardedBy("statusLock")
    private volatile FeedStatus status = FeedStatus.UNKNOWN;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean keepAlive = new AtomicBoolean(false);
    private final Map<String,MarketDataRequestToken> handleMap = new ConcurrentHashMap<String,MarketDataRequestToken>();
    private final Multimap<MarketDataRequestToken,String> internalHandlesByToken = HashMultimap.create();
    private volatile MarketDataProviderRegistry providerRegistry;
    private final BlockingDeque<EventNotification> notifications = new LinkedBlockingDeque<EventNotification>();
    private volatile EventNotifier notifier;
}
