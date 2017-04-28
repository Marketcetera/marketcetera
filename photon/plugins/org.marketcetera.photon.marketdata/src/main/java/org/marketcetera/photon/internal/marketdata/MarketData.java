package org.marketcetera.photon.internal.marketdata;

import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.HasInstrument;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.AssetClass;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.core.manager.MarketDataProviderNotAvailable;
import org.marketcetera.marketdata.core.manager.MarketDataRequestFailed;
import org.marketcetera.marketdata.core.manager.MarketDataRequestTimedOut;
import org.marketcetera.marketdata.core.manager.NoMarketDataProvidersAvailable;
import org.marketcetera.marketdata.core.webservice.ConnectionException;
import org.marketcetera.marketdata.core.webservice.UnknownRequestException;
import org.marketcetera.photon.marketdata.IMarketData;
import org.marketcetera.photon.marketdata.IMarketDataReference;
import org.marketcetera.photon.model.marketdata.MDDepthOfBook;
import org.marketcetera.photon.model.marketdata.MDItem;
import org.marketcetera.photon.model.marketdata.MDLatestTick;
import org.marketcetera.photon.model.marketdata.MDMarketstat;
import org.marketcetera.photon.model.marketdata.MDQuote;
import org.marketcetera.photon.model.marketdata.MDTopOfBook;
import org.marketcetera.photon.model.marketdata.impl.MDDepthOfBookImpl;
import org.marketcetera.photon.model.marketdata.impl.MDLatestTickImpl;
import org.marketcetera.photon.model.marketdata.impl.MDMarketstatImpl;
import org.marketcetera.photon.model.marketdata.impl.MDQuoteImpl;
import org.marketcetera.photon.model.marketdata.impl.MDTopOfBookImpl;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/* $License$ */

/**
 * This class is the central manager of market data.
 * 
 * <p>All requests come through this class. Market data is returned in the form of a reference that can be disposed when no
 * longer needed. This allow data flows to be shared so at most one is active for any given data request. Data flows are started after the first request
 * and stopped after the last reference is disposed.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class MarketData
        implements IMarketData
{
    /**
     * Create a new MarketData instance.
     *
     * @param inMarketDataClientProvider an <code>IMarketDataClientProvider</code> value
     */
    @Inject
    public MarketData(final IMarketDataClientProvider inMarketDataClientProvider)
    {
        Validate.notNull(inMarketDataClientProvider);
        marketDataClientProvider = inMarketDataClientProvider;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.marketdata.IMarketData#reset()
     */
    @Override
    public void reset()
    {
        if(refreshJobToken != null) {
            refreshJobToken.cancel(true);
            refreshJobToken = null;
        }
        scheduleRefreshJob();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.marketdata.IMarketData#resubmit()
     */
    @Override
    public void resubmit()
    {
        // TODO don't do anything if the reconnect button was pressed but no connection was lost to the server
        for(Entry<MarketDataReferenceKey,MarketDataDetails<?,?>> entry : requests.asMap().entrySet()) {
            MarketDataDetails<?,?> marketDataDetails = entry.getValue();
            marketDataDetails.disconnect();
            marketDataDetails.connect();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.marketdata.IMarketData#getLatestTick(org.marketcetera.trade.Instrument)
     */
    @Override
    public IMarketDataReference<MDLatestTick> getLatestTick(Instrument inInstrument)
    {
        return getMarketDataReference(inInstrument,
                                      Content.LATEST_TICK,
                                      latestTickFactory,
                                      latestTickUpdater);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.marketdata.IMarketData#getTopOfBook(org.marketcetera.trade.Instrument)
     */
    @Override
    public IMarketDataReference<MDTopOfBook> getTopOfBook(Instrument inInstrument)
    {
        return getMarketDataReference(inInstrument,
                                      Content.TOP_OF_BOOK,
                                      topOfBookFactory,
                                      topOfBookUpdater);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.marketdata.IMarketData#getMarketstat(org.marketcetera.trade.Instrument)
     */
    @Override
    public IMarketDataReference<MDMarketstat> getMarketstat(Instrument inInstrument)
    {
        return getMarketDataReference(inInstrument,
                                      Content.MARKET_STAT,
                                      marketstatFactory,
                                      marketstatUpdater);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.marketdata.IMarketData#getDepthOfBook(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content)
     */
    @Override
    public IMarketDataReference<MDDepthOfBook> getDepthOfBook(Instrument inInstrument,
                                                              Content inProduct)
    {
        switch(inProduct) {
            case AGGREGATED_DEPTH:
                return getMarketDataReference(inInstrument,
                                              Content.AGGREGATED_DEPTH,
                                              depthOfBookFactory,
                                              aggregatedDepthUpdater);
            case BBO10:
                return getMarketDataReference(inInstrument,
                                              Content.BBO10,
                                              depthOfBookFactory,
                                              bbo10DepthUpdater);
            case LEVEL_2:
                return getMarketDataReference(inInstrument,
                                              Content.LEVEL_2,
                                              depthOfBookFactory,
                                              level2DepthUpdater);
            case OPEN_BOOK:
                return getMarketDataReference(inInstrument,
                                              Content.OPEN_BOOK,
                                              depthOfBookFactory,
                                              openBookDepthUpdater);
            case TOTAL_VIEW:
                return getMarketDataReference(inInstrument,
                                              Content.TOTAL_VIEW,
                                              depthOfBookFactory,
                                              totalViewDepthUpdater);
            case UNAGGREGATED_DEPTH:
                return getMarketDataReference(inInstrument,
                                              Content.UNAGGREGATED_DEPTH,
                                              depthOfBookFactory,
                                              unaggregatedDepthUpdater);
            case MARKET_STAT:
            case NBBO:
            case TOP_OF_BOOK:
            case DIVIDEND:
            case LATEST_TICK:
            default :
                throw new UnsupportedOperationException();
        }
    }
    /**
     * Schedule the market data refresh job, if necessary.
     */
    private void scheduleRefreshJob()
    {
        synchronized(refreshJobLock) {
            if(refreshJobToken == null) {
                refreshJobToken = marketDataRefreshExecutor.scheduleAtFixedRate(new MetaRefreshJob(),
                                                                                UPDATE_FREQUENCY,
                                                                                UPDATE_FREQUENCY,
                                                                                TimeUnit.MILLISECONDS);
            }
        }
    }
    /**
     * Gets a market data reference for the given attributes.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @param inFactory an <code>ItemFactory&lt;MDMutableType&gt;</code> value
     * @param inUpdater an <code>ItemUpdated&lt;MDMutableType&gt;</code> value
     * @return an <code>IMarketDataReference&lt;MDType&gt;</code> value
     */
    @SuppressWarnings("unchecked")
    private <MDType extends MDItem,MDMutableType extends MDType> IMarketDataReference<MDType> getMarketDataReference(final Instrument inInstrument,
                                                                                                                     final Content inContent,
                                                                                                                     ItemFactory<MDMutableType> inFactory,
                                                                                                                     final ItemUpdater<MDMutableType> inUpdater)
    {
        Validate.notNull(inInstrument);
        Validate.notNull(inContent);
        final MarketDataReferenceKey key = new MarketDataReferenceKey(inInstrument,
                                                                      inContent);
        MarketDataDetails<?,?> existingMarketDataDetails = requests.getIfPresent(key);
        if(existingMarketDataDetails != null) {
            // somebody has already asked for this data, simply return the existing reference
            existingMarketDataDetails.incrementReferenceCount();
            return (IMarketDataReference<MDType>)existingMarketDataDetails.getReference();
        }
        // this is a new reference
        MarketDataRequestBuilder builder = MarketDataRequestBuilder.newRequest()
            .withAssetClass(AssetClass.getFor(inInstrument.getSecurityType()))
            .withSymbols(inInstrument.getFullSymbol())
            .withContent(inContent);
        MarketDataRequest request = builder.create();
        MDMutableType item = inFactory.create();
        MarketDataDetails<MDType,MDMutableType> newMarketDataDetails = new MarketDataDetails<>(inInstrument,
                                                                                               inContent,
                                                                                               request,
                                                                                               item,
                                                                                               key,
                                                                                               inUpdater);
        newMarketDataDetails.incrementReferenceCount();
        requests.put(key,
                     newMarketDataDetails);
        newMarketDataDetails.connect();
        return newMarketDataDetails.getReference();
    }
    /**
     * Manages refreshing all subscription data.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class MetaRefreshJob
            implements Runnable
    {
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            try {
                for(SubscriptionRefreshJob<?,?> refreshJob : refreshJobs) {
                    try {
                        refreshJob.refreshJobToken = marketDataRefreshExecutor.submit(refreshJob);
                    } catch (Exception e) {
                        SLF4JLoggerProxy.warn(org.marketcetera.core.Messages.USER_MSG_CATEGORY,
                                              e,
                                              "A problem occurred updating market data for {} {} [{}]",
                                              refreshJob.content,
                                              refreshJob.instrument,
                                              refreshJob.id);
                    }
                }
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(org.marketcetera.core.Messages.USER_MSG_CATEGORY,
                                      e);
            }
        }
    }
    /**
     * Manages a market data subscription and applies updates.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.0
     */
    @ClassVersion("$Id$")
    private class SubscriptionRefreshJob<MDType extends MDItem,MDMutableType extends MDType>
            implements Runnable
    {
        /**
         * Create a new SubscriptionRefreshJob instance.
         *
         * @param inInstrument an <code>Instrument</code> value
         * @param inContent a <code>Content</code> value
         * @param inId a <code>long</code> value
         * @param inUpdater an <code>ItemUpdater&lt;MDMutableType&gt;</code> value
         * @param inItem an <code>MDMutableType</code> value
         */
        private SubscriptionRefreshJob(Instrument inInstrument,
                                       Content inContent,
                                       long inId,
                                       ItemUpdater<MDMutableType> inUpdater,
                                       MDMutableType inItem)
        {
            instrument = inInstrument;
            content = inContent;
            id = inId;
            updater = inUpdater;
            item = inItem;
        }
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            try {
                if(marketDataClientProvider.getMarketDataClient() == null || !marketDataClientProvider.getMarketDataClient().isRunning()) {
                    updater.clear(item);
                    cancel();
                    return;
                }
                Deque<Event> events = marketDataClientProvider.getMarketDataClient().getSnapshot(instrument,
                                                                                                 content,
                                                                                                 null);
                updater.update(item,
                               events);
            } catch (UnknownRequestException e) {
                // this is likely a transient problem, which will sort itself out shortly
                SLF4JLoggerProxy.warn(org.marketcetera.core.Messages.USER_MSG_CATEGORY,
                                      e);
                cancel();
            } catch (ConnectionException e) {
                // exception caused by a lost connection - cancel this request
                SLF4JLoggerProxy.error(org.marketcetera.core.Messages.USER_MSG_CATEGORY,
                                       "Market Data Nexus {}",
                                       ExceptionUtils.getRootCauseMessage(e));
                updater.clear(item);
                cancel();
            } catch (Exception e) {
                // this is an exception that occurred during update - must be caught or it kills the scheduled executor - log it and report a problem to the user
                updater.clear(item);
                SLF4JLoggerProxy.error(org.marketcetera.core.Messages.USER_MSG_CATEGORY,
                                       "A problem occurred updating market data for {} {} [{}]: {}",
                                       content,
                                       instrument,
                                       id,
                                       ExceptionUtils.getRootCauseMessage(e));
            }
        }
        /**
         * Cancels this job from being run again.
         */
        private void cancel()
        {
            if(refreshJobToken != null) {
                refreshJobToken.cancel(true);
                refreshJobToken = null;
            }
        }
        /**
         * market data request instrument
         */
        private final Instrument instrument;
        /**
         * market data request content
         */
        private final Content content;
        /**
         * server request id
         */
        private final long id;
        /**
         * updater which updates {@link #item} when new events come in
         */
        private final ItemUpdater<MDMutableType> updater;
        /**
         * item to update when new events come in
         */
        private final MDMutableType item;
        /**
         * reference token for the scheduled market data refresh job, if <code>null</code>, no job is scheduled
         */
        private Future<?> refreshJobToken;
    }
    /**
     * Uniquely identifies the contents of a market data request.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.0
     */
    @ClassVersion("$Id$")
    private static class MarketDataReferenceKey
    {
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            return new HashCodeBuilder().append(content).append(instrument).toHashCode();
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
            if (!(obj instanceof MarketDataReferenceKey)) {
                return false;
            }
            MarketDataReferenceKey other = (MarketDataReferenceKey) obj;
            return new EqualsBuilder().append(other.content,content).append(other.instrument,instrument).isEquals();
        }
        /**
         * Create a new MarketDataReferenceKey instance.
         *
         * @param inInstrument
         * @param inContent
         */
        private MarketDataReferenceKey(Instrument inInstrument,
                                       Content inContent)
        {
            instrument = inInstrument;
            content = inContent;
        }
        /**
         * market data instrument requested
         */
        private final Instrument instrument;
        /**
         * market data content requested
         */
        private final Content content;
    }
    /**
     * Contains the market data reference for an instrument-content tuple.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.0
     */
    @ClassVersion("$Id$")
    private class MarketDataDetails<MDType extends MDItem,MDMutableItemType extends MDType>
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return new StringBuilder().append(instrument.getFullSymbol()).append(" - ").append(content).append(" ").append(referenceCount.get()).append(" use(s) [").append(requestId).append("]").toString();
        }
        /**
         * Disconnects this market data unit from the market data nexus while preserving the underlying reference.
         * 
         * <p>This market data unit may be reconnected later and all existing users
         * of the underlying reference will transparently pick up the new market data deliveries.
         */
        private void disconnect()
        {
            updater.clear(item);
            if(refreshJob != null) {
                refreshJobs.remove(refreshJob);
                refreshJob.cancel();
                refreshJob = null;
            }
            // do NOT change the reference count because there are still some number of users of the underlying reference
            try {
                marketDataClientProvider.getMarketDataClient().cancel(requestId);
                requestId = -1;
            } catch (Exception ignored) {}
        }
        /**
         * Render this market data unit no longer usable.
         */
        private void dispose()
        {
            requests.invalidate(key);
            refreshJobs.remove(refreshJob);
            disconnect();
        }
        /**
         * Connects this market data unit to the market data nexus without affecting the underlying resource.
         */
        private void connect()
        {
            if(marketDataClientProvider.getMarketDataClient() == null || !marketDataClientProvider.getMarketDataClient().isRunning()) {
                // we could connect at a later time
                return;
            }
            try {
                requestId = marketDataClientProvider.getMarketDataClient().request(request,
                                                                                   false);
            } catch (NoMarketDataProvidersAvailable | MarketDataProviderNotAvailable | MarketDataRequestTimedOut e) {
                SLF4JLoggerProxy.error(org.marketcetera.core.Messages.USER_MSG_CATEGORY,
                                       "Request for {} - {} failed because no market data providers are available that can honor the request",
                                       content,
                                       instrument);
                SLF4JLoggerProxy.error(this,
                                       e,
                                       "Request for {} - {} failed because no market data providers are available that can honor the request",
                                       content,
                                       instrument);
                return;
            } catch (MarketDataRequestFailed e) {
                SLF4JLoggerProxy.error(org.marketcetera.core.Messages.USER_MSG_CATEGORY,
                                       "Request for {} - {} failed because there was a problem with the request",
                                       content,
                                       instrument);
                SLF4JLoggerProxy.error(this,
                                       e,
                                       "Request for {} - {} failed because there was a problem with the request",
                                       content,
                                       instrument);
                return;
            } catch (Exception e) {
                SLF4JLoggerProxy.error(org.marketcetera.core.Messages.USER_MSG_CATEGORY,
                                       "Request for {} - {} failed",
                                       content,
                                       instrument);
                SLF4JLoggerProxy.error(this,
                                       e,
                                       "Request for {} - {} failed",
                                       content,
                                       instrument);
                throw new RuntimeException(e);
            }
            refreshJob = new SubscriptionRefreshJob<>(instrument,
                                                      content,
                                                      requestId,
                                                      updater,
                                                      item);
            refreshJobs.add(refreshJob);
            scheduleRefreshJob();
        }
        /**
         * Increments and returns the reference count.
         *
         * @return an <code>int</code> value containing the updated reference count
         */
        private int incrementReferenceCount()
        {
            return referenceCount.incrementAndGet();
        }
        /**
         * Decrements and returns the reference count.
         *
         * @return an <code>int</code> value containing the updated the reference count
         */
        private int decrementReferenceCount()
        {
            int updatedCount = referenceCount.decrementAndGet();
            if(updatedCount <= 0) {
                updatedCount = 0;
                referenceCount.set(0);
                dispose();
            }
            return updatedCount;
        }
        /**
         * Get the reference value.
         *
         * @return an <code>IMarketDataReference<TypeClazz></code> value
         */
        private IMarketDataReference<MDType> getReference()
        {
            return reference;
        }
        /**
         * Create a new MarketDataDetails instance.
         *
         * @param inInstrument an <code>Instrument</code> value
         * @param inContent a <code>Content</code> value
         * @param inRequest a <code>MarketDataRequest</code> value
         * @param inItem an <code>MDMutableItemType</code> value
         * @param inKey a <code>MarketDataReferenceKey</code> value
         * @param inUpdater an <code>ItemUpdated&lt;MDMutableItemType&gt;</code> value
         */
        private MarketDataDetails(Instrument inInstrument,
                                  Content inContent,
                                  MarketDataRequest inRequest,
                                  final MDMutableItemType inItem,
                                  final MarketDataReferenceKey inKey,
                                  ItemUpdater<MDMutableItemType> inUpdater)
        {
            instrument = inInstrument;
            content = inContent;
            request = inRequest;
            item = inItem;
            updater = inUpdater;
            key = inKey;
            reference = new IMarketDataReference<MDType>() {
                @Override
                public MDType get()
                {
                    return item;
                }
                @Override
                public void dispose()
                {
                    // this method is called if a consumer of the reference no longer needs it
                    decrementReferenceCount();
                }
                /* (non-Javadoc)
                 * @see java.lang.Object#toString()
                 */
                @Override
                public String toString()
                {
                    return new StringBuilder().append("Ref-").append(id).append(" ").append(content).append(" for ").append(instrument).append(" [").append(requestId).append("]").toString();
                }
                /**
                 * id assigned to this market data reference, used to uniquely identify a reference, not really used for anything else
                 */
                private long id = System.nanoTime();
            };
        }
        /**
         * updates {@link #item} when new events arrive
         */
        private final ItemUpdater<MDMutableItemType> updater;
        /**
         * item to update when market data changes arrive
         */
        private final MDMutableItemType item;
        /**
         * market data request ID returned from the nexus
         */
        private volatile long requestId;
        /**
         * market data request used to create this reference
         */
        private final MarketDataRequest request;
        /**
         * job responsible for updating the market data contents of this reference
         */
        private volatile SubscriptionRefreshJob<MDType,MDMutableItemType> refreshJob;
        /**
         * tracks active user count
         */
        private final AtomicInteger referenceCount = new AtomicInteger(0);
        /**
         * market data instrument requested
         */
        private final Instrument instrument;
        /**
         * market data content requested
         */
        private final Content content;
        /**
         * market data key which uniquely identifies the reference contents
         */
        private final MarketDataReferenceKey key;
        /**
         * market data source
         */
        private final IMarketDataReference<MDType> reference;
    }
    /**
     * Creates a market data type value.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.0
     */
    @ClassVersion("$Id$")
    private interface ItemFactory<MDType extends MDItem>
    {
        /**
         * Creates a new market data type value.
         *
         * @return an <code>MDType</code> value
         */
        MDType create();
    }
    /**
     * Updates an item with new events.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.0
     */
    @ClassVersion("$Id$")
    private interface ItemUpdater<MDType extends MDItem>
    {
        /**
         * Updates the given item based on the given events, sorted most recent to least recent.
         *
         * @param inItem an <code>MDType</code> value
         * @param inEvents a <code>Deque&lt;Event&gt;</code> value
         */
        void update(MDType inItem,
                    Deque<Event> inEvents);
        /**
         * Clears the given item.
         *
         * @param inItem an <code>MDType</code> value
         */
        void clear(MDType inItem);
    }
    /**
     * Updates depth-of-book items.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.0
     */
    @ClassVersion("$Id$")
    private abstract class AbstractDepthUpdater
            implements ItemUpdater<MDDepthOfBookImpl>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.photon.internal.marketdata.MarketData.ItemUpdater#update(org.marketcetera.photon.model.marketdata.MDItem, java.util.Deque)
         */
        @Override
        public void update(final MDDepthOfBookImpl inItem,
                           final Deque<Event> inEvents)
        {
            if(inEvents == null || inEvents.isEmpty()) {
                clear(inItem);
                return;
            }
            Event firstEvent = inEvents.getFirst();
            HasInstrument hasInstrument = (HasInstrument)firstEvent;
            Instrument instrument = hasInstrument.getInstrument();
            inItem.setInstrument(instrument);
            inItem.setProduct(getContent());
            final List<AskEvent> newAsks = Lists.newArrayList();
            final List<BidEvent> newBids = Lists.newArrayList();
            for(Event event : inEvents) {
                if(event instanceof BidEvent) {
                    newBids.add((BidEvent)event);
                } else if(event instanceof AskEvent) {
                    newAsks.add((AskEvent)event);
                } else {
                    throw new UnsupportedOperationException();
                }
            }
            inItem.getAsks().doWriteOperation(new Callable<Void>() {
                @Override
                public Void call()
                        throws Exception
                {
                    doListUpdate(inItem.getAsks(),
                                 newAsks);
                    return null;
                }
            });
            inItem.getBids().doWriteOperation(new Callable<Void>() {
                @Override
                public Void call()
                        throws Exception
                {
                    doListUpdate(inItem.getBids(),
                                 newBids);
                    return null;
                }
            });
        }
        /* (non-Javadoc)
         * @see org.marketcetera.photon.internal.marketdata.MarketData.ItemUpdater#clear(org.marketcetera.photon.model.marketdata.MDItem)
         */
        @Override
        public void clear(MDDepthOfBookImpl inItem)
        {
            inItem.getBids().clear();
            inItem.getAsks().clear();
        }
        /**
         * Update the given current list with the contents of the given new list.
         *
         * @param inCurrentList a <code>LockableEList&lt;MDQuote&gt;</code> value
         * @param inNewList a <code>List&lt;? extends QuoteEvent&gt;</code> value
         */
        private void doListUpdate(LockableEList<MDQuote> inCurrentList,
                                  List<? extends QuoteEvent> inNewList)
        {
            Iterator<MDQuote> currentEventIterator = inCurrentList.iterator();
            Iterator<? extends QuoteEvent> newEventIterator = inNewList.iterator();
            List<MDQuote> quotesToRemove = Lists.newArrayList();
            while(currentEventIterator.hasNext()) {
                MDQuote currentEvent = currentEventIterator.next();
                QuoteEvent newEvent = null;
                if(newEventIterator.hasNext()) {
                    newEvent = newEventIterator.next();
                }
                // currentEvent is non-null, newEvent may or may not be null
                if(newEvent == null) {
                    // current has no match in the new list
                    quotesToRemove.add(currentEvent);
                } else {
                    // there is an entry in both the new list and the old list, compare them to see if they're the same
                    if(!isEqual(currentEvent,newEvent)) {
                        ((MDQuoteImpl)currentEvent).setPrice(newEvent.getPrice());
                        ((MDQuoteImpl)currentEvent).setSize(newEvent.getSize());
                        ((MDQuoteImpl)currentEvent).setSource(newEvent.getExchange());
                        ((MDQuoteImpl)currentEvent).setTime(newEvent.getQuoteDate().getTime());
                    }
                }
            }
            inCurrentList.removeAll(quotesToRemove);
            // anything left in the new event iterator is new
            if(newEventIterator.hasNext()) {
                List<MDQuote> newItems = Lists.newArrayList();
                while(newEventIterator.hasNext()) {
                    QuoteEvent newQuote = newEventIterator.next();
                    MDQuoteImpl quoteItem = new MDQuoteImpl();
                    quoteItem.setInstrument(newQuote.getInstrument());
                    quoteItem.setPrice(newQuote.getPrice());
                    quoteItem.setSize(newQuote.getSize());
                    quoteItem.setSource(newQuote.getExchange());
                    quoteItem.setTime(newQuote.getQuoteDate().getTime());
                    newItems.add(quoteItem);
                }
                inCurrentList.addAll(newItems);
            }
        }
        /**
         * Determine if the two quote objects are effectively equal.
         *
         * @param inMdQuote an <code>MDQuote</code> value
         * @param inQuote a <code>QuoteEvent</code> value
         * @return a <code>boolean</code> value
         */
        private boolean isEqual(MDQuote inMdQuote,
                                QuoteEvent inQuote)
        {
            if(inMdQuote.getTime() != inQuote.getQuoteDate().getTime()) {
                return false;
            }
            if(inMdQuote.getPrice().compareTo(inQuote.getPrice()) != 0) {
                return false;
            }
            if(inMdQuote.getSize().compareTo(inQuote.getSize()) != 0) {
                return false;
            }
            return true;
        }
        /**
         * Gets the content type of this market data updater.
         *
         * @return a <code>Content</code> value
         */
        protected abstract Content getContent();
    }
    /**
     * creates a latest-execution item
     */
    private ItemFactory<MDLatestTickImpl> latestTickFactory = new ItemFactory<MDLatestTickImpl>() {
        @Override
        public MDLatestTickImpl create()
        {
            return new MDLatestTickImpl();
        }
    };
    /**
     * creates a top-of-book item
     */
    private ItemFactory<MDTopOfBookImpl> topOfBookFactory = new ItemFactory<MDTopOfBookImpl>() {
        @Override
        public MDTopOfBookImpl create()
        {
            return new MDTopOfBookImpl();
        }
    };
    /**
     * creates a marketstat item
     */
    private ItemFactory<MDMarketstatImpl> marketstatFactory = new ItemFactory<MDMarketstatImpl>() {

        @Override
        public MDMarketstatImpl create()
        {
            return new MDMarketstatImpl();
        }
    };
    /**
     * creates a depth-of-book item
     */
    private ItemFactory<MDDepthOfBookImpl> depthOfBookFactory = new ItemFactory<MDDepthOfBookImpl>() {

        @Override
        public MDDepthOfBookImpl create()
        {
            return new MDDepthOfBookImpl();
        }
    };
    /**
     * manages latest execution updates
     */
    private ItemUpdater<MDLatestTickImpl> latestTickUpdater = new ItemUpdater<MDLatestTickImpl>() {
        @Override
        public void update(MDLatestTickImpl inItem,
                           Deque<Event> inEvents)
        {
            if(inEvents == null || inEvents.isEmpty()) {
                clear(inItem);
                return;
            }
            for(Event event : inEvents) {
                if(event instanceof TradeEvent) {
                    TradeEvent trade = (TradeEvent)event;
                    inItem.setInstrument(trade.getInstrument());
                    inItem.setPrice(trade.getPrice());
                    inItem.setSize(trade.getSize());
                }
            }
        }
        @Override
        public void clear(MDLatestTickImpl inItem)
        {
            inItem.setPrice(null);
            inItem.setSize(null);
        }
    };
    /**
     * manages top-of-book updates
     */
    private ItemUpdater<MDTopOfBookImpl> topOfBookUpdater = new ItemUpdater<MDTopOfBookImpl>() {
        @Override
        public void update(MDTopOfBookImpl inItem,
                           Deque<Event> inEvents)
        {
            if(inEvents == null || inEvents.isEmpty()) {
                clear(inItem);
                return;
            }
            boolean askFound = false;
            boolean bidFound = false;
            for(Event quote : inEvents) {
                if(quote instanceof AskEvent) {
                    AskEvent ask = (AskEvent)quote;
                    inItem.setInstrument(ask.getInstrument());
                    inItem.setAskPrice(ask.getPrice());
                    inItem.setAskSize(ask.getSize());
                    askFound = true;
                } else if(quote instanceof BidEvent) {
                    BidEvent bid = (BidEvent)quote;
                    inItem.setInstrument(bid.getInstrument());
                    inItem.setBidPrice(bid.getPrice());
                    inItem.setBidSize(bid.getSize());
                    bidFound = true;
                }
            }
            if(!bidFound) {
                clearBid(inItem);
            }
            if(!askFound) {
                clearAsk(inItem);
            }
        }
        @Override
        public void clear(MDTopOfBookImpl inItem)
        {
            clearAsk(inItem);
            clearBid(inItem);
        }
        /**
         * Clear the bid price.
         *
         * @param inItem an <code>MDTopOfBookImpl</code> value
         */
        private void clearBid(MDTopOfBookImpl inItem)
        {
            inItem.setBidPrice(null);
            inItem.setBidSize(null);
        }
        /**
         * Clear the ask price.
         *
         * @param inItem an <code>MDTopOfBookImpl</code> value
         */
        private void clearAsk(MDTopOfBookImpl inItem)
        {
            inItem.setAskPrice(null);
            inItem.setAskSize(null);
        }
    };
    /**
     * manages market stat updates
     */
    private ItemUpdater<MDMarketstatImpl> marketstatUpdater = new ItemUpdater<MDMarketstatImpl>() {
        @Override
        public void update(MDMarketstatImpl inItem,
                           Deque<Event> inEvents)
        {
            if(inEvents == null || inEvents.isEmpty()) {
                clear(inItem);
                return;
            }
            for(Event event : inEvents) {
                if(event instanceof MarketstatEvent) {
                    MarketstatEvent statEvent = (MarketstatEvent)event;
                    inItem.setInstrument(statEvent.getInstrument());
                    inItem.setLowPrice(statEvent.getHigh()); // this is high price
                    inItem.setHighPrice(statEvent.getOpen()); // this is open/close
                    inItem.setOpenPrice(statEvent.getVolume()); // this is actually trade volume
                    inItem.setVolumeTraded(statEvent.getLow()); // this is actually low price
                    inItem.setPreviousClosePrice(statEvent.getPreviousClose()); // this is right!
                }
            }
        }
        @Override
        public void clear(MDMarketstatImpl inItem)
        {
            inItem.setCloseDate(null);
            inItem.setClosePrice(null);
            inItem.setHighPrice(null);
            inItem.setLowPrice(null);
            inItem.setOpenPrice(null);
            inItem.setPreviousCloseDate(null);
            inItem.setPreviousClosePrice(null);
            inItem.setVolumeTraded(null);
        }
    };
    /**
     * manages aggregated depth updates
     */
    private ItemUpdater<MDDepthOfBookImpl> aggregatedDepthUpdater = new AbstractDepthUpdater() {
        @Override
        protected Content getContent()
        {
            return Content.AGGREGATED_DEPTH;
        }
    };
    /**
     * manages unaggregated depth updates
     */
    private ItemUpdater<MDDepthOfBookImpl> unaggregatedDepthUpdater = new AbstractDepthUpdater() {
        @Override
        protected Content getContent()
        {
            return Content.UNAGGREGATED_DEPTH;
        }
    };
    /**
     * manages bbo10 depth updates
     */
    private ItemUpdater<MDDepthOfBookImpl> bbo10DepthUpdater = new AbstractDepthUpdater() {
        @Override
        protected Content getContent()
        {
            return Content.BBO10;
        }
    };
    /**
     * manages level II depth updates
     */
    private ItemUpdater<MDDepthOfBookImpl> level2DepthUpdater = new AbstractDepthUpdater() {
        @Override
        protected Content getContent()
        {
            return Content.LEVEL_2;
        }
    };
    /**
     * manages open book depth updates
     */
    private ItemUpdater<MDDepthOfBookImpl> openBookDepthUpdater = new AbstractDepthUpdater() {
        @Override
        protected Content getContent()
        {
            return Content.OPEN_BOOK;
        }
    };
    /**
     * manages total view depth updates
     */
    private ItemUpdater<MDDepthOfBookImpl> totalViewDepthUpdater = new AbstractDepthUpdater() {
        @Override
        protected Content getContent()
        {
            return Content.TOTAL_VIEW;
        }
    };
    /**
     * holds current market data refresh jobs
     */
    private final Set<SubscriptionRefreshJob<?,?>> refreshJobs = Sets.newHashSet();
    /**
     * contains market data active requests by reference key (for reuse)
     */
    private final Cache<MarketDataReferenceKey,MarketDataDetails<?,?>> requests = CacheBuilder.newBuilder().build();
    /**
     * schedules market data refresh jobs
     */
    private final ScheduledExecutorService marketDataRefreshExecutor = Executors.newScheduledThreadPool(10);
    /**
     * provides access to the market data client
     */
    private final IMarketDataClientProvider marketDataClientProvider;
    /**
     * indicates how frequently to check for market data updates (in ms)
     */
    private static final long UPDATE_FREQUENCY = 1000;
    /**
     * guards access to {@link #refreshJobToken}
     */
    private final Object refreshJobLock = new Object();
    /**
     * tracks the market data refresh job
     */
    private volatile ScheduledFuture<?> refreshJobToken = null;
}
