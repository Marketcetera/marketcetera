package org.marketcetera.photon.internal.marketdata;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.Validate;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.photon.marketdata.IMarketData;
import org.marketcetera.photon.marketdata.IMarketDataFeed;
import org.marketcetera.photon.marketdata.IMarketDataReference;
import org.marketcetera.photon.model.marketdata.MDDepthOfBook;
import org.marketcetera.photon.model.marketdata.MDItem;
import org.marketcetera.photon.model.marketdata.MDLatestTick;
import org.marketcetera.photon.model.marketdata.MDMarketstat;
import org.marketcetera.photon.model.marketdata.MDTopOfBook;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multiset;
import com.google.inject.Inject;

/* $License$ */

/**
 * This class is the central manager of market data. Individual sub-managers are
 * used for each data type, but all requests come through this class. Market
 * data is returned in the form of a reference that can be disposed when no
 * longer needed. This allow data flows to be shared so at most one is active
 * for any given data request. Data flows are started after the first request
 * and stopped after the last reference is disposed.
 * <p>
 * The sub-managers all extend {@link DataFlowManager}:
 * <ul>
 * <li>{@link LatestTickManager} - for latest tick data</li>
 * <li>{@link TopOfBookManager} - for top of book data</li>
 * <li>{@link MarketstatManager} - for market statistic data</li>
 * <li>{@link DepthOfBookManager} - for depth of book data (separate instances
 * manages each of Level 2, TotalView, and OpenBook data)</li>
 * <li>{@link SharedOptionLatestTickManager} - for option latest tick data when
 * fine grained market data is unavailable</li>
 * </ul>
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class MarketData implements IMarketData {

    private final Multiset<Key> mReferences = HashMultiset.create();
    private final ILatestTickManager mLatestTickManager;
    private final ITopOfBookManager mTopOfBookManager;
    private final IMarketstatManager mMarketstatManager;
    private final IDepthOfBookManager mLevel2Manager;
    private final IDepthOfBookManager mTotalViewManager;
    private final IDepthOfBookManager mOpenBookManager;
    private final ISharedOptionLatestTickManager mSharedOptionLatestTickManager;
    private final ISharedOptionMarketstatManager mSharedOptionMarketstatManager;
    private final Map<Content, IDepthOfBookManager> mContentToDepthManager;
    private final boolean mUseFineGrainedMarketDataForOptions;

    /**
     * Constructor.
     * 
     * @param latestTickManager
     *            the manager for latest tick requests
     * @param topOfBookManager
     *            the manager for top of book requests
     * @param marketstatManager
     *            the manager for statistic requests
     * @param depthOfBookManagerFactory
     *            the factory for creating market depth managers
     * @param sharedOptionLatestTickManager
     *            the manager for shared option latest tick data
     * @param mSharedOptionMarketstatManager
     *            the manager for shared option marketstat data
     * @param marketDataRequestAdapter
     *            controls whether fine grained market data is available for
     *            options
     * @throws IllegalArgumentException
     *             if any parameter is null, or if the depth of book factory
     *             returns null for a needed capability set
     */
    @Inject
    public MarketData(final ILatestTickManager latestTickManager,
            final ITopOfBookManager topOfBookManager,
            final IMarketstatManager marketstatManager,
            final IDepthOfBookManager.Factory depthOfBookManagerFactory,
            final ISharedOptionLatestTickManager sharedOptionLatestTickManager,
            final ISharedOptionMarketstatManager sharedOptionMarketstatManager,
            final IMarketDataRequestSupport marketDataRequestAdapter) {
        Validate.noNullElements(new Object[] { latestTickManager,
                topOfBookManager, marketstatManager, depthOfBookManagerFactory,
                sharedOptionLatestTickManager, sharedOptionMarketstatManager,
                marketDataRequestAdapter });
        mLatestTickManager = latestTickManager;
        mTopOfBookManager = topOfBookManager;
        mMarketstatManager = marketstatManager;
        mLevel2Manager = depthOfBookManagerFactory.create(EnumSet
                .of(Capability.LEVEL_2));
        mTotalViewManager = depthOfBookManagerFactory.create(EnumSet
                .of(Capability.TOTAL_VIEW));
        mOpenBookManager = depthOfBookManagerFactory.create(EnumSet
                .of(Capability.OPEN_BOOK));
        mSharedOptionLatestTickManager = sharedOptionLatestTickManager;
        mSharedOptionMarketstatManager = sharedOptionMarketstatManager;
        Validate.noNullElements(new Object[] { mLevel2Manager,
                mTotalViewManager, mOpenBookManager });
        mContentToDepthManager = ImmutableMap.of(Content.LEVEL_2,
                mLevel2Manager, Content.TOTAL_VIEW, mTotalViewManager,
                Content.OPEN_BOOK, mOpenBookManager);
        mUseFineGrainedMarketDataForOptions = marketDataRequestAdapter
                .useFineGrainedMarketDataForOptions();
    }

    /**
     * Sets the source for all data.
     * 
     * @param feed
     *            the source feed
     * @throws IllegalArgumentException
     *             if feed is null
     */
    public void setSourceFeed(final IMarketDataFeed feed) {
        mLatestTickManager.setSourceFeed(feed);
        mTopOfBookManager.setSourceFeed(feed);
        mMarketstatManager.setSourceFeed(feed);
        mLevel2Manager.setSourceFeed(feed);
        mTotalViewManager.setSourceFeed(feed);
        mOpenBookManager.setSourceFeed(feed);
        mSharedOptionLatestTickManager.setSourceFeed(feed);
        mSharedOptionMarketstatManager.setSourceFeed(feed);
    }

    /**
     * If true, we can request a separate flow for the instrument. If false, the
     * instrument is an Option and we must use sift out data from the shared
     * flow for the underlying.
     */
    private boolean canRequestFineGrainedMarketData(Instrument instrument) {
        return (instrument instanceof Equity)
                || mUseFineGrainedMarketDataForOptions;
    }

    @Override
    public IMarketDataReference<MDLatestTick> getLatestTick(
            final Instrument instrument) {
        Validate.notNull(instrument);
        if (canRequestFineGrainedMarketData(instrument)) {
            return new Reference<MDLatestTick, LatestTickKey>(
                    mLatestTickManager, new LatestTickKey(instrument));
        } else {
            final Option option = (Option) instrument;
            return getSharedReference(new SharedOptionLatestTickKey(new Equity(
                    option.getSymbol())), new LatestTickKey(option),
                    mSharedOptionLatestTickManager);
        }
    }

    private <T extends MDItem, I extends T, S extends Key, K extends Key, M extends IDataFlowManager<Map<Option, I>, S>> IMarketDataReference<T> getSharedReference(
            S sharedKey, K individualKey, M manager) {
        /*
         * The shared data case is complex. All shared option data items for the
         * same underlying equity share the same data flow (since fine grained
         * requests are not possible). Two reference counters are used.
         * 
         * One is for the actual market data flow keyed by the underlying equity
         * (S). Every request increments this counter and once the final
         * reference is disposed, the data flow can be terminated.
         * 
         * The second is for the market data item tied to a particular option
         * (K). While there are active references for this key, the shared
         * manager will update the market data item from the shared data flow.
         * Once the final reference is disposed, the shared data manager will
         * ignore those updates.
         * 
         * For example, if latest tick has been requested once for IBMoption1
         * and twice for IBMoption2, there will be three references for the IBM
         * SharedOptionLatestTickKey, one for the IBMoption1 LatestTickKey, and
         * two for the IBMoption2 LatestTickKey.
         */
        final Reference<Map<Option, I>, S> shared = new Reference<Map<Option, I>, S>(
                manager, sharedKey);
        /*
         * This will create a new data item the first time, but reuse the
         * existing one on successive invocations.
         */
        final T item = shared.get().get((Option) individualKey.getInstrument());
        return new AbstractReference<T, K>(individualKey, item) {
            @Override
            protected void referenceDisposed(K key, boolean lastOne) {
                if (lastOne) {
                    /*
                     * Cleans up the data item for this particular option.
                     */
                    shared.get().remove(key.getInstrument());
                }
                /*
                 * Always decrement the shared data flow reference count.
                 */
                shared.dispose();
            }
        };
    }

    @Override
    public IMarketDataReference<MDTopOfBook> getTopOfBook(
            final Instrument instrument) {
        Validate.notNull(instrument);
        return new Reference<MDTopOfBook, TopOfBookKey>(mTopOfBookManager,
                new TopOfBookKey(instrument));
    }

    @Override
    public IMarketDataReference<MDMarketstat> getMarketstat(
            final Instrument instrument) {
        Validate.notNull(instrument);
        if (canRequestFineGrainedMarketData(instrument)) {
            return new Reference<MDMarketstat, MarketstatKey>(
                    mMarketstatManager, new MarketstatKey(instrument));
        } else {
            final Option option = (Option) instrument;
            return getSharedReference(new SharedOptionMarketstatKey(new Equity(
                    option.getSymbol())), new MarketstatKey(option),
                    mSharedOptionMarketstatManager);
        }
    }

    @Override
    public IMarketDataReference<MDDepthOfBook> getDepthOfBook(
            final Instrument instrument, final Content product) {
        Validate.noNullElements(new Object[] { instrument, product });
        Validate.isTrue(DepthOfBookKey.VALID_PRODUCTS.contains(product));
        IDepthOfBookManager manager = mContentToDepthManager.get(product);
        return new Reference<MDDepthOfBook, DepthOfBookKey>(manager,
                new DepthOfBookKey(instrument, product));
    }

    /**
     * A reference to a market data item connected to a data flow.
     */
    @ClassVersion("$Id$")
    private class Reference<T, K extends Key> implements
            IMarketDataReference<T> {

        private final IMarketDataReference<T> mWrappedReference;

        public Reference(final IDataFlowManager<? extends T, K> manager,
                final K key) {
            assert manager != null && key != null;
            mWrappedReference = new AbstractReference<T, K>(key, manager
                    .getItem(key)) {
                @Override
                public void firstReferenceCreated(K key) {
                    manager.startFlow(key);
                }

                @Override
                protected void referenceDisposed(K key, boolean lastOne) {
                    if (lastOne) {
                        manager.stopFlow(key);
                    }
                }
            };
        }

        @Override
        public T get() {
            return mWrappedReference.get();
        }

        @Override
        public void dispose() {
            mWrappedReference.dispose();
        }
    }

    /**
     * A abstract {@link IMarketDataReference} implementation.
     */
    @ClassVersion("$Id$")
    private abstract class AbstractReference<T, K extends Key> implements
            IMarketDataReference<T> {

        private final T mItem;
        private final K mKey;
        private final AtomicBoolean mDisposed = new AtomicBoolean();

        public AbstractReference(final K key, final T item) {
            assert key != null;
            synchronized (mReferences) {
                if (!mReferences.contains(key)) {
                    firstReferenceCreated(key);
                }
                mReferences.add(key);
            }
            mKey = key;
            mItem = item;
        }

        /**
         * Called if the reference is the first for the key.
         * 
         * @param key
         *            the key for which the reference was created
         */
        protected void firstReferenceCreated(K key) {
        }

        /**
         * Called when the reference is disposed.
         * 
         * @param key
         *            the key for which the reference was created
         * @param lastOne
         *            true if this is the final reference for the key
         */
        protected void referenceDisposed(K key, boolean lastOne) {
        }

        @Override
        public final T get() {
            return mDisposed.get() ? null : mItem;
        }

        @Override
        public final void dispose() {
            if (mDisposed.getAndSet(true)) {
                return;
            }
            synchronized (mReferences) {
                if (!mReferences.remove(mKey)) {
                    /*
                     * Should always be able to remove the reference if not yet
                     * disposed.
                     */
                    throw new AssertionError();
                }
                if (!mReferences.contains(mKey)) {
                    referenceDisposed(mKey, true);
                } else {
                    referenceDisposed(mKey, false);
                }
            }
        }
    }
}
