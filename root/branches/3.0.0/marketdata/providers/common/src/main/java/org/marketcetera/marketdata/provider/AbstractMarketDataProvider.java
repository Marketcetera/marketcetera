package org.marketcetera.marketdata.provider;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.api.systemmodel.Instrument;
import org.marketcetera.api.systemmodel.Subscriber;
import org.marketcetera.core.publisher.PublisherEngine;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.MarketDataProvider;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.events.Event;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 * TODO need to handle restart case such that existing subscriptions are honored (resubmit and replace internal handles)
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
            // TODO interrupt any threads we have
            doStop();
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
     * @see org.marketcetera.marketdata.MarketDataProvider#requestMarketData(org.marketcetera.marketdata.MarketDataRequest, org.marketcetera.marketdata.MarketDataToken)
     */
    @Override
    public Collection<Event> requestMarketData(MarketDataRequest inRequest,
                                               Subscriber inSubscriber)
            throws InterruptedException
    {
        if(!isRunning()) {
            throw new IllegalStateException(getProviderName() + " is not running"); // TODO
        }
        Set<MarketDataRequestAtom> atoms = explodeRequest(inRequest);
        Collection<Event> snapshotEvents = new ArrayList<Event>();
        Set<String> internalHandles = new HashSet<String>();
        AtomicBoolean updateSemaphore = new AtomicBoolean(false);
        // TODO examine request atoms and make sure they can all be processed by the given provider (check capabilities and handled security types)
        try {
            for(MarketDataRequestAtom atom : atoms) {
                internalHandles.add(doMarketDataRequest(inRequest,
                                                        atom,
                                                        updateSemaphore,
                                                        snapshotEvents));
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
            // TODO throw RequestFailed exception
        }
        if(inSubscriber != null) {
            for(String internalHandle : internalHandles) {
                handleMap.put(internalHandle,
                              inSubscriber);
            }
        }
        updateSemaphore.set(true);
        return snapshotEvents;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataProvider#requestMarketData(org.marketcetera.marketdata.MarketDataRequest)
     */
    @Override
    public Collection<Event> requestMarketData(MarketDataRequest inRequest)
            throws InterruptedException
    {
        return requestMarketData(inRequest,
                                 null);
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
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.Publisher#subscribe(org.marketcetera.api.systemmodel.Subscriber)
     */
    @Override
    public void subscribe(Subscriber inSubscriber)
    {
        statusPublisher.subscribe(inSubscriber);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.Publisher#unsubscribe(org.marketcetera.api.systemmodel.Subscriber)
     */
    @Override
    public void unsubscribe(Subscriber inSubscriber)
    {
        statusPublisher.unsubscribe(inSubscriber);
    }
    /**
     * 
     *
     *
     * @param inInternalHandles
     * @param inEvents
     */
    protected void dataReceived(Set<String> inInternalHandles,
                                Object inEvents)
    {
        // TODO push this into a separate thread to allow events to queue here
        for(String handle : inInternalHandles) {
            Subscriber subscriber = handleMap.get(handle);
            if(subscriber != null &&
               subscriber.isInteresting(inEvents)) {
                subscriber.publishTo(inEvents);
            }
        }
    }
    /**
     * 
     *
     *
     * @param inInternalHandle
     * @param inEvents
     */
    protected void dataReceived(String inInternalHandle,
                                Object inEvents)
    {
        // TODO push this into a separate thread to allow events to queue here
        Subscriber subscriber = handleMap.get(inInternalHandle);
        if(subscriber != null &&
                subscriber.isInteresting(inEvents)) {
            subscriber.publishTo(inEvents);
        }
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
            statusPublisher.publish(inNewStatus);
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
     * @param outSnapshot
     * @return
     * @throws InterruptedException
     */
    protected abstract String doMarketDataRequest(MarketDataRequest inCompleteRequest,
                                                  MarketDataRequestAtom inRequestAtom,
                                                  AtomicBoolean inUpdateSemaphore,
                                                  Collection<Event> outSnapshot)
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
    private final PublisherEngine statusPublisher = new PublisherEngine();
    private final Map<String,Subscriber> handleMap = new ConcurrentHashMap<String,Subscriber>();
}
