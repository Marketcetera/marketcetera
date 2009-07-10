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
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multiset;
import com.google.inject.Inject;

/* $License$ */

/**
 * Manages references to market data items, starting and stopping data flows as needed.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class MarketData implements IMarketData {

	/*
	 * This class is the central manager of market data. Individual sub-managers are used for each 
	 * data type, but all requests come through this class. Market data is returned in the form
	 * of a reference that can be disposed when no longer needed.  This allow data flows to be
	 * shared so at most one is active for any given data key.
	 */
	
	private final Multiset<Key<? extends MDItem>> mReferences = HashMultiset.create();
	private final ILatestTickManager mLatestTickManager;
	private final ITopOfBookManager mTopOfBookManager;
	private final IMarketstatManager mMarketstatManager;
	private final IDepthOfBookManager mLevel2Manager;
	private final IDepthOfBookManager mTotalViewManager;
	private final IDepthOfBookManager mOpenBookManager;
	private final Map<Content, IDepthOfBookManager> mContentToDepthManager;

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
	 * @throws IllegalArgumentException
	 *             if any parameter is null, or if the depth of book factory returns null for a
	 *             needed capability set
	 */
	@Inject
	public MarketData(ILatestTickManager latestTickManager, ITopOfBookManager topOfBookManager,
			IMarketstatManager marketstatManager,
			IDepthOfBookManager.Factory depthOfBookManagerFactory) {
		Validate.noNullElements(new Object[] { latestTickManager, topOfBookManager,
				marketstatManager, depthOfBookManagerFactory });
		mLatestTickManager = latestTickManager;
		mTopOfBookManager = topOfBookManager;
		mMarketstatManager = marketstatManager;
		mLevel2Manager = depthOfBookManagerFactory.create(EnumSet.of(Capability.LEVEL_2));
		mTotalViewManager = depthOfBookManagerFactory.create(EnumSet.of(Capability.TOTAL_VIEW));
		mOpenBookManager = depthOfBookManagerFactory.create(EnumSet.of(Capability.OPEN_BOOK));
		Validate
				.noNullElements(new Object[] { mLevel2Manager, mTotalViewManager, mOpenBookManager });
		mContentToDepthManager = ImmutableMap.of(Content.LEVEL_2, mLevel2Manager,
				Content.TOTAL_VIEW, mTotalViewManager, Content.OPEN_BOOK, mOpenBookManager);
	}

	/**
	 * Sets the source for all data.
	 * 
	 * @param feed
	 *            the source feed
	 * @throws IllegalArgumentException
	 *             if feed is null
	 */
	public void setSourceFeed(IMarketDataFeed feed) {
		mLatestTickManager.setSourceFeed(feed);
		mTopOfBookManager.setSourceFeed(feed);
		mMarketstatManager.setSourceFeed(feed);
		mLevel2Manager.setSourceFeed(feed);
		mTotalViewManager.setSourceFeed(feed);
		mOpenBookManager.setSourceFeed(feed);
	}

	@Override
	public IMarketDataReference<MDLatestTick> getLatestTick(String symbol) {
		Validate.notNull(symbol);
		return new Reference<MDLatestTick, LatestTickKey>(mLatestTickManager,
					new LatestTickKey(symbol));
	}

	@Override
	public IMarketDataReference<MDTopOfBook> getTopOfBook(String symbol) {
		Validate.notNull(symbol);
		return new Reference<MDTopOfBook, TopOfBookKey>(mTopOfBookManager, new TopOfBookKey(
					symbol));
	}

	@Override
	public IMarketDataReference<MDMarketstat> getMarketstat(String symbol) {
		Validate.notNull(symbol);
		return new Reference<MDMarketstat, MarketstatKey>(mMarketstatManager,
					new MarketstatKey(symbol));
	}

	@Override
	public IMarketDataReference<MDDepthOfBook> getDepthOfBook(String symbol, Content product) {
		Validate.noNullElements(new Object[] { symbol, product });
		Validate.isTrue(DepthOfBookKey.VALID_PRODUCTS.contains(product));
		IDepthOfBookManager manager = mContentToDepthManager.get(product);
		return new Reference<MDDepthOfBook, DepthOfBookKey>(manager, new DepthOfBookKey(symbol,
					product));
	}

	/**
	 * A reference to a market data item.
	 */
	@ClassVersion("$Id$")
	private class Reference<T extends MDItem, K extends Key<T>> implements IMarketDataReference<T> {

		private final IDataFlowManager<? extends T, K> mManager;
		private final T mItem;
		private final K mKey;
		private AtomicBoolean mDisposed = new AtomicBoolean();

		public Reference(IDataFlowManager<? extends T, K> manager, K key) {
			assert manager != null && key != null;
			mManager = manager;
			synchronized (mReferences) {
				if (!mReferences.contains(key)) {
					mManager.startFlow(key);
				}
				mReferences.add(key);
			}
			mItem = manager.getItem(key);
			mKey = key;
		}

		@Override
		public T get() {
			return mDisposed.get() ? null : mItem;
		}

		@Override
		public void dispose() {
			if (mDisposed.getAndSet(true)) {
				return;
			}
			synchronized (mReferences) {
				if (!mReferences.remove(mKey)) {
					// should always be able to remove the reference if not yet disposed
					throw new AssertionError();
				}
				if (!mReferences.contains(mKey)) {
					mManager.stopFlow(mKey);
				}
			}
		}
	}
}
