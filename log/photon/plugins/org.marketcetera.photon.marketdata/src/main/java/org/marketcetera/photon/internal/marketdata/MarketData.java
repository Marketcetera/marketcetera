package org.marketcetera.photon.internal.marketdata;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.event.*;
import org.marketcetera.marketdata.AssetClass;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.core.manager.MarketDataProviderNotAvailable;
import org.marketcetera.marketdata.core.manager.MarketDataRequestFailed;
import org.marketcetera.marketdata.core.manager.MarketDataRequestTimedOut;
import org.marketcetera.marketdata.core.manager.NoMarketDataProvidersAvailable;
import org.marketcetera.marketdata.core.webservice.ConnectionException;
import org.marketcetera.marketdata.core.webservice.PageRequest;
import org.marketcetera.marketdata.core.webservice.UnknownRequestException;
import org.marketcetera.photon.marketdata.IMarketData;
import org.marketcetera.photon.marketdata.IMarketDataReference;
import org.marketcetera.photon.model.marketdata.*;
import org.marketcetera.photon.model.marketdata.impl.*;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
//        requests.clear();
        if(marketDataRefreshExecutor != null) {
            marketDataRefreshExecutor.shutdownNow();
        }
        marketDataRefreshExecutor = Executors.newScheduledThreadPool(10);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.marketdata.IMarketData#resubmit()
     */
    @Override
    public void resubmit()
    {
        // TODO don't do anything if the reconnect button was pressed but no connection was lost to the server
        for(Entry<MarketDataReferenceKey,MarketDataDetails<?,?>> entry : requests.entrySet()) {
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
                                      latestTickUpdater,
                                      TOP_UPDATE_FREQUENCY);
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
                                      topOfBookUpdater,
                                      TOP_UPDATE_FREQUENCY);
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
                                      marketstatUpdater,
                                      TOP_UPDATE_FREQUENCY);
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
                                              aggregatedDepthUpdater,
                                              DEPTH_UPDATE_FREQUENCY);
            case BBO10:
                return getMarketDataReference(inInstrument,
                                              Content.BBO10,
                                              depthOfBookFactory,
                                              bbo10DepthUpdater,
                                              DEPTH_UPDATE_FREQUENCY);
            case LEVEL_2:
                return getMarketDataReference(inInstrument,
                                              Content.LEVEL_2,
                                              depthOfBookFactory,
                                              level2DepthUpdater,
                                              DEPTH_UPDATE_FREQUENCY);
            case OPEN_BOOK:
                return getMarketDataReference(inInstrument,
                                              Content.OPEN_BOOK,
                                              depthOfBookFactory,
                                              openBookDepthUpdater,
                                              DEPTH_UPDATE_FREQUENCY);
            case TOTAL_VIEW:
                return getMarketDataReference(inInstrument,
                                              Content.TOTAL_VIEW,
                                              depthOfBookFactory,
                                              totalViewDepthUpdater,
                                              DEPTH_UPDATE_FREQUENCY);
            case UNAGGREGATED_DEPTH:
                return getMarketDataReference(inInstrument,
                                              Content.UNAGGREGATED_DEPTH,
                                              depthOfBookFactory,
                                              unaggregatedDepthUpdater,
                                              DEPTH_UPDATE_FREQUENCY);
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
     * Gets a market data reference for the given attributes.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @param inFactory an <code>ItemFactory&lt;MDMutableType&gt;</code> value
     * @param inUpdater an <code>ItemUpdated&lt;MDMutableType&gt;</code> value
     * @param inUpdateFrequency a <code>long</code> value
     * @return an <code>IMarketDataReference&lt;MDType&gt;</code> value
     */
    @SuppressWarnings("unchecked")
    private <MDType extends MDItem,MDMutableType extends MDType> IMarketDataReference<MDType> getMarketDataReference(final Instrument inInstrument,
                                                                                                                     final Content inContent,
                                                                                                                     ItemFactory<MDMutableType> inFactory,
                                                                                                                     final ItemUpdater<MDMutableType> inUpdater,
                                                                                                                     final long inUpdateFrequency)
    {
        Validate.notNull(inInstrument);
        Validate.notNull(inContent);
        final MarketDataReferenceKey key = new MarketDataReferenceKey(inInstrument,
                                                                      inContent);
        MarketDataDetails<?,?> existingMarketDataDetails = requests.get(key);
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
                                                                                               inUpdater,
                                                                                               inUpdateFrequency);
        newMarketDataDetails.incrementReferenceCount();
        requests.put(key,
                     newMarketDataDetails);
        newMarketDataDetails.connect();
        return newMarketDataDetails.getReference();
    }
    /**
     * Manages a market data subscription and applies updates.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
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
                long updateTimestamp = marketDataClientProvider.getMarketDataClient().getLastUpdate(id);
                if(updateTimestamp > lastUpdate) {
                    // TODO switch to get snapshot page
                    PageRequest page = new PageRequest(1,
                                                       Integer.MAX_VALUE);
                    Deque<Event> events = marketDataClientProvider.getMarketDataClient().getSnapshotPage(instrument,
                                                                                                         content,
                                                                                                         null,
                                                                                                         page);
                    if(events == null || events.isEmpty()) {
                        return;
                    }
                    updater.update(item,
                                   events);
                    lastUpdate = updateTimestamp;
                }
            } catch (UnknownRequestException e) {
                // this is likely a transient problem, which will sort itself out shortly
                SLF4JLoggerProxy.warn(MarketData.this,
                                      e);
                cancel();
            } catch (ConnectionException e) {
                // exception caused by a lost connection - cancel this request
                SLF4JLoggerProxy.error(MarketData.this,
                                       e);
                updater.clear(item);
                cancel();
            } catch (Exception e) {
                // this is an exception that occurred during update - must be caught or it kills the scheduled executor - log it and report a problem to the user
                updater.clear(item);
                SLF4JLoggerProxy.error(org.marketcetera.core.Messages.USER_MSG_CATEGORY,
                                       "A problem occurred updating market data for {} {} [{}]",
                                       content,
                                       instrument,
                                       id);
                SLF4JLoggerProxy.error(MarketData.this,
                                       e);
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
        /**
         * tracks the last update time
         */
        private long lastUpdate = 0;
    }
    /**
     * Uniquely identifies the contents of a market data request.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
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
     * @since $Release$
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
            requests.remove(key);
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
            refreshJob.refreshJobToken = marketDataRefreshExecutor.scheduleAtFixedRate(refreshJob,
                                                                                       1000,
                                                                                       updateFrequency,
                                                                                       TimeUnit.MILLISECONDS);
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
         * @param inUpdateFrequency a <code>long</code> value
         */
        private MarketDataDetails(Instrument inInstrument,
                                  Content inContent,
                                  MarketDataRequest inRequest,
                                  final MDMutableItemType inItem,
                                  final MarketDataReferenceKey inKey,
                                  ItemUpdater<MDMutableItemType> inUpdater,
                                  long inUpdateFrequency)
        {
            instrument = inInstrument;
            content = inContent;
            request = inRequest;
            item = inItem;
            updater = inUpdater;
            updateFrequency = inUpdateFrequency;
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
         * indicates how frequently (in ms) to check for updates
         */
        private final long updateFrequency;
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
     * @since $Release$
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
     * @since $Release$
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
     * @since $Release$
     */
    @ClassVersion("$Id$")
    private abstract static class AbstractDepthUpdater
            implements ItemUpdater<MDDepthOfBookImpl>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.photon.internal.marketdata.MarketData.ItemUpdater#update(org.marketcetera.photon.model.marketdata.MDItem, java.util.Deque)
         */
        @Override
        public void update(final MDDepthOfBookImpl inItem,
                           final Deque<Event> inEvents)
        {
            inItem.setInstrument(inItem.getInstrument());
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
            // TODO measure difference only
            if(!newAsks.isEmpty()) {
                inItem.getAsks().doWriteOperation(new Callable<Void>() {
                    @Override
                    public Void call()
                            throws Exception
                    {
                        inItem.getAsks().clear();
                        for(AskEvent ask : newAsks) {
                            MDQuoteImpl quoteItem = new MDQuoteImpl();
                            quoteItem.setInstrument(ask.getInstrument());
                            quoteItem.setPrice(ask.getPrice());
                            quoteItem.setSize(ask.getSize());
                            quoteItem.setSource(String.valueOf(ask.getSource()));
                            quoteItem.setTime(ask.getTimeMillis());
                            inItem.getAsks().add(quoteItem);
                        }
                        return null;
                    }
                });
            }
            if(!newBids.isEmpty()) {
                inItem.getBids().doWriteOperation(new Callable<Void>() {
                    @Override
                    public Void call()
                            throws Exception
                    {
                        inItem.getBids().clear();
                        for(BidEvent bid : newBids) {
                            MDQuoteImpl quoteItem = new MDQuoteImpl();
                            quoteItem.setInstrument(bid.getInstrument());
                            quoteItem.setPrice(bid.getPrice());
                            quoteItem.setSize(bid.getSize());
                            quoteItem.setSource(String.valueOf(bid.getSource()));
                            quoteItem.setTime(bid.getTimeMillis());
                            inItem.getBids().add(quoteItem);
                        }
                        return null;
                    }
                });
            }
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
            Event mostRecentEvent = inEvents.getFirst();
            if(mostRecentEvent instanceof TradeEvent) {
                TradeEvent trade = (TradeEvent)mostRecentEvent;
                inItem.setInstrument(trade.getInstrument());
                inItem.setPrice(trade.getPrice());
                inItem.setSize(trade.getSize());
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
            // traverse the list from front to back, stopping when we have one each of bid and ask
            boolean askFound = false;
            boolean bidFound = false;
            for(Event event : inEvents) {
                if(event instanceof AskEvent) {
                    AskEvent ask = (AskEvent)event;
                    askFound = true;
                    inItem.setInstrument(ask.getInstrument());
                    inItem.setAskPrice(ask.getPrice());
                    inItem.setAskSize(ask.getSize());
                } else if(event instanceof BidEvent) {
                    BidEvent bid = (BidEvent)event;
                    bidFound = true;
                    inItem.setInstrument(bid.getInstrument());
                    inItem.setBidPrice(bid.getPrice());
                    inItem.setBidSize(bid.getSize());
                }
                if(askFound && bidFound) {
                    break;
                }
            }
        }
        @Override
        public void clear(MDTopOfBookImpl inItem)
        {
            inItem.setAskPrice(null);
            inItem.setAskSize(null);
            inItem.setBidPrice(null);
            inItem.setBidSize(null);
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
            Event mostRecentEvent = inEvents.getFirst();
            if(mostRecentEvent instanceof MarketstatEvent) {
                MarketstatEvent statEvent = (MarketstatEvent)mostRecentEvent;
                inItem.setInstrument(statEvent.getInstrument());
                inItem.setCloseDate(statEvent.getCloseDate());
                inItem.setClosePrice(statEvent.getClose());
                inItem.setHighPrice(statEvent.getHigh());
                inItem.setLowPrice(statEvent.getLow());
                inItem.setOpenPrice(statEvent.getOpen());
                inItem.setPreviousCloseDate(statEvent.getPreviousCloseDate());
                inItem.setPreviousClosePrice(statEvent.getPreviousClose());
                inItem.setVolumeTraded(statEvent.getVolume());
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
     * contains market data active requests by reference key (for reuse)
     */
    private final Map<MarketDataReferenceKey,MarketDataDetails<?,?>> requests = Maps.newHashMap();
    /**
     * schedules market data refresh jobs
     */
    private ScheduledExecutorService marketDataRefreshExecutor = Executors.newScheduledThreadPool(10);
    /**
     * provides access to the market data client
     */
    private final IMarketDataClientProvider marketDataClientProvider;
    /**
     * indicates how frequently to check for top-of-book market data updates (in ms)
     */
    private static final long TOP_UPDATE_FREQUENCY = 1000;
    /**
     * indicates how frequently to check for market data depth updates (in ms)
     */
    private static final long DEPTH_UPDATE_FREQUENCY = 3000;
}
