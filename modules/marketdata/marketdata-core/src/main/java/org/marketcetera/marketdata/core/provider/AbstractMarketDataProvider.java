package org.marketcetera.marketdata.core.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventType;
import org.marketcetera.event.HasEventType;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.core.MarketDataProvider;
import org.marketcetera.marketdata.core.ProviderStatus;
import org.marketcetera.marketdata.core.cache.MarketDataCache;
import org.marketcetera.marketdata.core.manager.MarketDataException;
import org.marketcetera.marketdata.core.manager.MarketDataProviderNotAvailable;
import org.marketcetera.marketdata.core.manager.MarketDataProviderRegistry;
import org.marketcetera.marketdata.core.manager.MarketDataRequestFailed;
import org.marketcetera.marketdata.core.request.MarketDataRequestAtom;
import org.marketcetera.marketdata.core.request.MarketDataRequestToken;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.context.Lifecycle;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/* $License$ */

/**
 * Provides common behavior for market data providers.
 * 
 * <p>To create a market data provider, extend this class.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ThreadSafe
@ClassVersion("$Id$")
public abstract class AbstractMarketDataProvider
        implements MarketDataProvider,MarketDataCache
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.cache.MarketdataCache#getSnapshot(org.marketcetera.core.trade.Instrument, org.marketcetera.marketdata.Content)
     */
    @Override
    public Event getSnapshot(Instrument inInstrument,
                             Content inContent)
{
        Lock snapshotLock = marketdataLock.readLock();
        try {
            snapshotLock.lockInterruptibly();
            MarketdataCacheElement cachedData = cachedMarketdata.get(inInstrument);
            if(cachedData != null) {
                return cachedData.getSnapshot(inContent);
            }
            return null;
        } catch (InterruptedException e) {
            org.marketcetera.marketdata.core.Messages.UNABLE_TO_ACQUIRE_LOCK.error(this);
            stop();
            throw new MarketDataRequestFailed(e);
        } finally {
            snapshotLock.unlock();
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    @PostConstruct
    public synchronized void start()
    {
        if(isRunning()) {
            stop();
        }
        try {
            doStart();
            totalRequests = 0;
            totalEvents = 0;
            instrumentsBySymbol.clear();
            cachedMarketdata.clear();
            notifications.clear();
            requestsByInstrument.clear();
            requestsByAtom.clear();
            requestsBySymbol.clear();
            notifier = new EventNotifier();
            notifier.start();
            running.set(true);
            setFeedStatus(ProviderStatus.AVAILABLE);
        } catch (Exception e) {
            setFeedStatus(ProviderStatus.ERROR);
            throw new MarketDataProviderStartFailed(e);
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    @PreDestroy
    public synchronized void stop()
    {
        if(!isRunning()) {
            return;
        }
        try {
            doStop();
            setFeedStatus(ProviderStatus.OFFLINE);
        } catch (Exception e) {
            setFeedStatus(ProviderStatus.ERROR);
        } finally {
            notifier.stop();
            instrumentsBySymbol.clear();
            cachedMarketdata.clear();
            notifications.clear();
            requestsByInstrument.clear();
            requestsByAtom.clear();
            requestsBySymbol.clear();
            running.set(false);
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
    {
        if(!isRunning()) {
            throw new MarketDataProviderNotAvailable();
        }
        Set<MarketDataRequestAtom> atoms = explodeRequest(inRequestToken.getRequest());
        totalRequests += atoms.size();
        SLF4JLoggerProxy.debug(this,
                               "Received market data request {}, exploded to {}", //$NON-NLS-1$
                               inRequestToken,
                               atoms);
        Lock marketdataRequestLock = marketdataLock.writeLock();
        try {
            marketdataRequestLock.lockInterruptibly();
        } catch (InterruptedException e) {
            org.marketcetera.marketdata.core.Messages.UNABLE_TO_ACQUIRE_LOCK.error(this);
            stop();
            throw new MarketDataRequestFailed(e);
        }
        SLF4JLoggerProxy.trace(this,
                               "Acquired lock"); //$NON-NLS-1$
        try {
            mapRequestToInstruments(inRequestToken);
            for(MarketDataRequestAtom atom : atoms) {
                if(requestsByAtom.containsKey(atom)) {
                    SLF4JLoggerProxy.debug(this,
                                           "Already requested {}, adding to reference count",
                                           atom);
                    Instrument snapshotInstrument = instrumentsBySymbol.get(atom.getSymbol());
                    if(snapshotInstrument == null) {
                        SLF4JLoggerProxy.warn(this,
                                              "Symbol {} not yet mapped, cannot send snapshot",
                                               atom.getSymbol());
                    } else {
                        Event snapshotEvent = getSnapshot(snapshotInstrument,
                                                          atom.getContent());
                        if(snapshotEvent instanceof HasEventType) {
                            HasEventType eventTypeSnapshot = (HasEventType)snapshotEvent;
                            eventTypeSnapshot.setEventType(EventType.SNAPSHOT_FINAL);
                        }
                        if(snapshotEvent != null) {
                            SLF4JLoggerProxy.debug(this,
                                                   "Sending snapshot: {}",
                                                   snapshotEvent);
                            if(inRequestToken.getSubscriber() != null) {
                                inRequestToken.getSubscriber().publishTo(snapshotEvent);
                            }
                        } else {
                            SLF4JLoggerProxy.debug(this,
                                                   "No snapshot for {}",
                                                   atom);
                        }
                    }
                    requestsByAtom.put(atom,
                                       inRequestToken);
                    requestsBySymbol.put(atom.getSymbol(),
                                         inRequestToken);
                } else {
                    Capability requiredCapability = necessaryCapabilities.get(atom.getContent());
                    if(requiredCapability == null) {
                        org.marketcetera.marketdata.core.Messages.UNKNOWN_MARKETDATA_CONTENT.error(this,
                                                                  atom.getContent());
                        throw new UnsupportedOperationException(org.marketcetera.marketdata.core.Messages.UNKNOWN_MARKETDATA_CONTENT.getText(atom.getContent()));
                    }
                    Set<Capability> capabilities = getCapabilities();
                    if(!capabilities.contains(requiredCapability)) {
                        org.marketcetera.marketdata.core.Messages.UNSUPPORTED_MARKETDATA_CONTENT.error(this,
                                                                                                       atom.getContent(),
                                                                                                       capabilities.toString());
                        throw new MarketDataRequestFailed(new I18NBoundMessage2P(org.marketcetera.marketdata.core.Messages.UNSUPPORTED_MARKETDATA_CONTENT,
                                                                                 atom.getContent(),
                                                                                 capabilities.toString()));
                    }
                    requestsByAtom.put(atom,
                                       inRequestToken);
                    requestsBySymbol.put(atom.getSymbol(),
                                         inRequestToken);
                    SLF4JLoggerProxy.debug(this,
                                           "Requesting {}",
                                           atom);
                    doMarketDataRequest(inRequestToken.getRequest(),
                                        atom);
                }
            }
        } catch (Exception e) {
            try {
                cancelMarketDataRequest(inRequestToken);
            } catch (Exception ignored) {}
            org.marketcetera.marketdata.core.Messages.MARKETDATA_REQUEST_FAILED.warn(this,
                                                                                     e);
            if(e instanceof MarketDataException) {
                throw (MarketDataException)e;
            }
            throw new MarketDataRequestFailed(e);
        } finally {
            marketdataRequestLock.unlock();
            SLF4JLoggerProxy.trace(this,
                                   "Lock released"); //$NON-NLS-1$
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.provider.MarketDataProvider#cancelMarketDataRequest(org.marketcetera.marketdata.request.MarketDataRequestToken)
     */
    @Override
    public void cancelMarketDataRequest(MarketDataRequestToken inRequestToken)
    {
        // TODO re-exploding the request might cause problems if the request itself changed, better to associate the token ID
        //  with a set of atoms
        Lock cancelLock = marketdataLock.writeLock();
        try {
            cancelLock.lockInterruptibly();
            Set<MarketDataRequestAtom> atoms = explodeRequest(inRequestToken.getRequest());
            for(MarketDataRequestAtom atom : atoms) {
                Collection<MarketDataRequestToken> symbolRequests = requestsByAtom.get(atom);
                if(symbolRequests != null) {
                    symbolRequests.remove(inRequestToken);
                    if(symbolRequests.isEmpty()) {
                        doCancel(atom);
                    }
                }
                Collection<MarketDataRequestToken> requests = requestsBySymbol.get(atom.getSymbol());
                if(requests != null) {
                    requests.remove(inRequestToken);
                }
                Instrument mappedInstrument = instrumentsBySymbol.get(atom.getSymbol());
                if(mappedInstrument != null) {
                    Collection<MarketDataRequestToken> instrumentRequests = requestsByInstrument.get(mappedInstrument);
                    if(instrumentRequests != null) {
                        instrumentRequests.remove(inRequestToken);
                        if(instrumentRequests.isEmpty()) {
                            // no more requests for this instrument, which means this instrument will no longer be updated - clear the cache for it
                            cachedMarketdata.remove(mappedInstrument);
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            org.marketcetera.marketdata.core.Messages.UNABLE_TO_ACQUIRE_LOCK.error(this);
            stop();
        } finally {
            cancelLock.unlock();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataProvider#getFeedStatus()
     */
    @Override
    public ProviderStatus getProviderStatus()
    {
        return status;
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
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.provider.MarketDataProviderMXBean#getTotalRequests()
     */
    @Override
    public int getTotalRequests()
    {
        return totalRequests;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.provider.MarketDataProviderMXBean#getActiveRequests()
     */
    @Override
    public int getActiveRequests()
    {
        Lock marketdataQueryLock = marketdataLock.readLock();
        try {
            marketdataQueryLock.lockInterruptibly();
        } catch (InterruptedException e) {
            org.marketcetera.marketdata.core.Messages.UNABLE_TO_ACQUIRE_LOCK.error(this);
            throw new MarketDataRequestFailed(e);
        }
        try {
            return requestsByAtom.size();
        } finally {
            marketdataQueryLock.unlock();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.provider.MarketDataProviderMXBean#getTotalEvents()
     */
    @Override
    public int getTotalEvents()
    {
        return totalEvents;
    }
    /**
     * Indicates that the given events have been received by the provider and should be sent to interested subscribers.
     *
     * @param inContent a <code>Content</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inEvents an <code>Event[]</code> value
     */
    protected void publishEvents(Content inContent,
                                 Instrument inInstrument,
                                 Event...inEvents)
    {
        // TODO validation: make sure each event has the proper content and instrument (don't do this every time, just if the provider requests validation)
        // TODO validation: make sure each instrument has a mapping
        totalEvents += inEvents.length;
        notifications.add(new EventNotification(inContent,
                                                inInstrument,
                                                inEvents));
    }
    /**
     * Creates a link between the given symbol and the given instrument.
     *
     * @param inSymbol a <code>String</code> value
     * @param inInstrument an <code>Instrument</code> value
     */
    protected void addSymbolMapping(String inSymbol,
                                    Instrument inInstrument)
    {
        SLF4JLoggerProxy.debug(this,
                               "Adding symbol mapping: {} -> {}",
                               inSymbol,
                               inInstrument);
        Lock symbolMappingLock = marketdataLock.writeLock();
        try {
            symbolMappingLock.lockInterruptibly();
            instrumentsBySymbol.put(inSymbol,
                                    inInstrument);
            Collection<MarketDataRequestToken> tokens = requestsBySymbol.get(inSymbol);
            for(MarketDataRequestToken token : tokens) {
                requestsByInstrument.put(inInstrument,
                                         token);
            }
        } catch (InterruptedException e) {
            org.marketcetera.marketdata.core.Messages.UNABLE_TO_ACQUIRE_LOCK.error(this);
            stop();
        } finally {
            symbolMappingLock.unlock();
        }
    }
    /**
     * 
     *
     * <p>This method requires an external write-lock on {@link #requestsByInstrument} and
     * an external read-lock on {@link #instrumentsBySymbol}.
     *
     * @param inToken
     */
    private void mapRequestToInstruments(MarketDataRequestToken inToken)
    {
        for(String symbol : inToken.getRequest().getSymbols()) {
            Instrument instrument = instrumentsBySymbol.get(symbol);
            if(instrument != null) {
                requestsByInstrument.put(instrument,
                                         inToken);
            }
        }
        for(String symbol : inToken.getRequest().getUnderlyingSymbols()) {
            Instrument instrument = instrumentsBySymbol.get(symbol);
            if(instrument != null) {
                requestsByInstrument.put(instrument,
                                         inToken);
            }
        }
    }
    /**
     * Sets the feed status value.
     *
     * @param inNewStatus a <code>FeedStatus</code> value
     */
    protected void setFeedStatus(ProviderStatus inNewStatus)
    {
        if(inNewStatus != status) {
            status = inNewStatus;
            if(providerRegistry != null) {
                providerRegistry.setStatus(this,
                                           inNewStatus);
            }
        }
    }
    /**
     * Indicates if the provider requests additional validation on the data it produces.
     *
     * <p>Subclasses <em>may</em> override this method to increase validation on its generated
     * event stream. Validation has a minor negative impact on latency. Subclasses should return
     * <code>true</code> during the development phase but should likely return <code>false</code>
     * for production. The default returned value is <code>false</code>.
     *
     * @return a <code>boolean</code> value
     */
    protected boolean doValidation()
    {
        return false;
    }
    /**
     * Starts the market data provider.
     */
    protected abstract void doStart();
    /**
     * Stops the market data provider.
     */
    protected abstract void doStop();
    /**
     * Cancels the market data request represented by the given request atom.
     *
     * @param inAtom a <code>MarketDataRequestAtom</code> value
     */
    protected abstract void doCancel(MarketDataRequestAtom inAtom);
    /**
     * Indicates to the market data provider that it should request market data for the given
     * <code>MarketDataRequestAtom</code>.
     *
     * <p>Note that the overall <code>MarketDataRequest</code> is provided, and can be used
     * for reference, but the provider should respond to the given <code>MarketDataRequestAtom</code>.
     *
     * @param inCompleteRequest a <code>MarketDataRequest</code> value
     * @param inRequestAtom a <code>MarketDataRequestAtom</code> value
     * @throws InterruptedException if the request cannot be executed
     */
    protected abstract void doMarketDataRequest(MarketDataRequest inCompleteRequest,
                                                MarketDataRequestAtom inRequestAtom)
            throws InterruptedException;
    /**
     * Gets the distinct market data request atoms from the given request.
     *
     * @param inRequest a <code>MarketDataRequest</code> value
     * @return a <code>Set&lt;MarketDataRequestAtomg&gt;</code> value
     */
    private Set<MarketDataRequestAtom> explodeRequest(MarketDataRequest inRequest)
    {
        Set<MarketDataRequestAtom> atoms = new LinkedHashSet<MarketDataRequestAtom>();
        if(inRequest.getSymbols().isEmpty()) {
            for(String underlyingSymbol : inRequest.getUnderlyingSymbols()) {
                for(Content content : inRequest.getContent()) {
                    atoms.add(new MarketDataRequestAtomImpl(underlyingSymbol,
                                                            inRequest.getExchange(),
                                                            content,
                                                            true));
                }
            }
        } else {
            for(String symbol : inRequest.getSymbols()) {
                for(Content content : inRequest.getContent()) {
                    atoms.add(new MarketDataRequestAtomImpl(symbol,
                                                            inRequest.getExchange(),
                                                            content,
                                                            false));
                }
            }
        }
        return atoms;
    }
    /**
     * Represents a single market data request item.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.0
     */
    @Immutable
    private static class MarketDataRequestAtomImpl
            implements MarketDataRequestAtom
    {
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.request.MarketDataRequestAtom#getSymbol()
         */
        @Override
        public String getSymbol()
        {
            return symbol;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.request.MarketDataRequestAtom#getExchange()
         */
        @Override
        public String getExchange()
        {
            return exchange;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.request.MarketDataRequestAtom#isUnderlyingSymbol()
         */
        @Override
        public boolean isUnderlyingSymbol()
        {
            return isUnderlyingSymbol;
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
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append(content).append(" : ").append(symbol); //$NON-NLS-1$
            if(exchange != null) {
                builder.append(" : ").append(exchange); //$NON-NLS-1$
            }
            if(isUnderlyingSymbol) {
                builder.append(" (underlying)"); //$NON-NLS-1$
            }
            return builder.toString();
        }
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            return new HashCodeBuilder().append(content).append(symbol).append(exchange).append(isUnderlyingSymbol).toHashCode();
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
            return new EqualsBuilder().append(symbol,other.symbol)
                                      .append(exchange,other.exchange)
                                      .append(content,other.content)
                                      .append(isUnderlyingSymbol,other.isUnderlyingSymbol).isEquals();
        }
        /**
         * Create a new MarketDataRequestAtomImpl instance.
         *
         * @param inSymbol a <code>String</code> value
         * @param inExchange a <code>String</code> value or <code>null</code>
         * @param inContent a <code>Content</code> value
         * @param inIsUnderlyingSymbol a <code>boolean</code> value
         */
        private MarketDataRequestAtomImpl(String inSymbol,
                                          String inExchange,
                                          Content inContent,
                                          boolean inIsUnderlyingSymbol)
        {
            symbol = inSymbol;
            exchange = inExchange;
            content = inContent;
            isUnderlyingSymbol = inIsUnderlyingSymbol;
        }
        /**
         * symbol value, may be a symbol, an underlying symbol, or a symbol fragment
         */
        private final String symbol;
        /**
         * exchange value or <code>null</code>
         */
        private final String exchange;
        /**
         * indicates if the symbol is supposed to be a symbol or an underlying symbol
         */
        private final boolean isUnderlyingSymbol;
        /**
         * content value of the request
         */
        private final Content content;
    }
    /**
     * Processes events returned by the provider and publishes them to interested subscribers.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.0
     */
    @ClassVersion("$Id$")
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
                Collection<MarketDataRequestToken> requests = new ArrayList<MarketDataRequestToken>();
                while(keepAlive.get()) {
                    running.set(true);
                    EventNotification notification = notifications.take();
                    Event[] events = notification.events;
                    if(events != null) {
                        // sort out where to apply these events. the key to the cached market data is the instrument
                        Instrument eventInstrument = notification.instrument;
                        // there is at least one event to process. let the market data cache process each event
                        MarketdataCacheElement marketdataCache = cachedMarketdata.get(eventInstrument);
                        if(marketdataCache == null) {
                            marketdataCache = new MarketdataCacheElement(eventInstrument);
                            cachedMarketdata.put(eventInstrument,
                                                 marketdataCache);
                        }
                        // we now have the market data cache object to use - give it the incoming events
                        Collection<Event> outgoingEvents = marketdataCache.update(notification.content,
                                                                                  events);
                        // find subscribers to this instrument
                        requests.clear();
                        Lock requestLock = marketdataLock.readLock();
                        try {
                            requestLock.lockInterruptibly();
                            // defensive copy to avoid chance of CME if a cancel is called while the processing is ongoing
                            requests.addAll(requestsByInstrument.get(eventInstrument));
                        } finally {
                            requestLock.unlock();
                        }
                        SLF4JLoggerProxy.trace("events.publishing",
                                               "Publishing {} to {}",
                                               outgoingEvents,
                                               requests);
                        for(MarketDataRequestToken requestToken : requests) {
                            // for each subscriber, determine if the request contents justifies the update
                            if(requestToken.getRequest().getContent().contains(notification.content)) {
                                // enclose the "publishTo" in a try/catch because we're ceding control to unknown code and
                                //  we don't want a misbehaving subscriber to break the market data mechanism
                                try {
                                    for(Event outgoingEvent : outgoingEvents) {
                                        outgoingEvent.setSource(requestToken.getId());
                                        outgoingEvent.setProvider(getProviderName());
                                        ISubscriber subscriber = requestToken.getSubscriber();
                                        if(subscriber != null && subscriber.isInteresting(outgoingEvent)) {
                                            subscriber.publishTo(outgoingEvent);
                                        }
                                    }
                                } catch (Exception e) {
                                    org.marketcetera.marketdata.core.Messages.EVENT_NOTIFICATION_FAILED.warn(AbstractMarketDataProvider.this,
                                                                                                             e,
                                                                                                             outgoingEvents,
                                                                                                             requestToken.getSubscriber());
                                }
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
            } finally {
                SLF4JLoggerProxy.debug(AbstractMarketDataProvider.this,
                                       "Event notifier for {} shutting down", //$NON-NLS-1$
                                       getProviderName());
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
                                "Market data notifier thread for " + getProviderName()); //$NON-NLS-1$
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
        /**
         * keeps the event notifier running
         */
        private final AtomicBoolean keepAlive = new AtomicBoolean(false);
        /**
         * indicates if the event notifier is running
         */
        private final AtomicBoolean running = new AtomicBoolean(false);
        /**
         * notifier thread
         */
        private volatile Thread thread;
    }
    /**
     * Represents an event notification to be published.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.0
     */
    @ClassVersion("$Id$")
    private static class EventNotification
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE).append("instrument",instrument).append("content",content).append(" [") //$NON-NLS-1$ //$NON-NLS-2$
                                                                             .append(Arrays.toString(events)).append(" ]").toString(); //$NON-NLS-1$
        }
        /**
         * Create a new EventNotification instance.
         *
         * @param inContent a <code>Content</code> value
         * @param inInstrument an <code>Instrument</code> value
         * @param inEvents an <code>Event[]</code> value
         */
        private EventNotification(Content inContent,
                                  Instrument inInstrument,
                                  Event... inEvents)
        {
            events = inEvents;
            content = inContent;
            instrument = inInstrument;
        }
        /**
         * content value
         */
        private final Content content;
        /**
         * instrument value
         */
        private final Instrument instrument;
        /**
         * events to notify
         */
        private final Event[] events;
    }
    /**
     * feed status value
     */
    private volatile ProviderStatus status = ProviderStatus.UNKNOWN;
    /**
     * indicates if the provider is running
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
    /**
     * provider registry value with which to register/unregister or <code>null</code>
     */
    private volatile MarketDataProviderRegistry providerRegistry;
    /**
     * total number of requests submitted
     */
    private volatile int totalRequests;
    /**
     * total number of events created
     */
    private volatile int totalEvents;
    /**
     * notification collection that contains events to publish
     */
    private final BlockingDeque<EventNotification> notifications = new LinkedBlockingDeque<EventNotification>();
    /**
     * processes events to be published and publishes them
     */
    private volatile EventNotifier notifier;
    /**
     * used to protect the market data collections
     */
    private final ReadWriteLock marketdataLock = new ReentrantReadWriteLock();
    /**
     * maps symbols or symbol fragments to the instrument value from the viewpoint of the market data provider
     */
    @GuardedBy("marketdataLock")
    private final Map<String,Instrument> instrumentsBySymbol = new HashMap<String,Instrument>();
    /**
     * tracks market data requests by the instrument in which they are interested
     */
    @GuardedBy("marketdataLock")
    private final Multimap<Instrument,MarketDataRequestToken> requestsByInstrument = HashMultimap.create();
    /**
     * tracks market data requests by the market data request atom created
     */
    @GuardedBy("marketdataLock")
    private final Multimap<MarketDataRequestAtom,MarketDataRequestToken> requestsByAtom = HashMultimap.create();
    /**
     * tracks market data requests by the symbol the request contained
     */
    @GuardedBy("marketdataLock")
    private final Multimap<String,MarketDataRequestToken> requestsBySymbol = HashMultimap.create();
    /**
     * tracks cached market data by the instrument
     */
    @GuardedBy("marketdataLock")
    private final Map<Instrument,MarketdataCacheElement> cachedMarketdata = new HashMap<Instrument,MarketdataCacheElement>();
    /**
     * maps the capabilities needed to honor a request of a particular content type
     */
    private static final Map<Content,Capability> necessaryCapabilities;
    /**
     * provides one-time initialization of static components
     */
    static
    {
        Map<Content,Capability> capabilities = new HashMap<Content,Capability>();
        capabilities.put(Content.DIVIDEND,Capability.DIVIDEND);
        capabilities.put(Content.LATEST_TICK,Capability.LATEST_TICK);
        capabilities.put(Content.LEVEL_2,Capability.LEVEL_2);
        capabilities.put(Content.MARKET_STAT,Capability.MARKET_STAT);
        capabilities.put(Content.OPEN_BOOK,Capability.OPEN_BOOK);
        capabilities.put(Content.TOP_OF_BOOK,Capability.TOP_OF_BOOK);
        capabilities.put(Content.TOTAL_VIEW,Capability.TOTAL_VIEW);
        capabilities.put(Content.AGGREGATED_DEPTH,Capability.AGGREGATED_DEPTH);
        capabilities.put(Content.UNAGGREGATED_DEPTH,Capability.UNAGGREGATED_DEPTH);
        capabilities.put(Content.IMBALANCE,Capability.IMBALANCE);
        necessaryCapabilities = Collections.unmodifiableMap(capabilities);
    }
}
