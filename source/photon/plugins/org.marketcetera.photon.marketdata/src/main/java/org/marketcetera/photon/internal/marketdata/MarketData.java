package org.marketcetera.photon.internal.marketdata;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.Validate;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.marketdata.IMarketData;
import org.marketcetera.photon.marketdata.IMarketDataReference;
import org.marketcetera.photon.model.marketdata.MDDepthOfBook;
import org.marketcetera.photon.model.marketdata.MDItem;
import org.marketcetera.photon.model.marketdata.MDLatestTick;
import org.marketcetera.photon.model.marketdata.MDMarketstat;
import org.marketcetera.photon.model.marketdata.MDTopOfBook;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.HashMultiset;
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
	 * Thread safety is provided by synchronizing access to the individual managers.
	 */

	private final Multiset<Key<? extends MDItem>> mReferences = HashMultiset.create();
	private final ILatestTickManager mLatestTickManager;
	private final ITopOfBookManager mTopOfBookManager;
	private final IMarketstatManager mMarketstatManager;
	private final IDepthOfBookManager mDepthOfBookManager;

	/**
	 * Constructor.
	 * 
	 * @param latestTickManager
	 *            the manager for latest tick requests
	 * @param topOfBookManager
	 *            the manager for top of book requests
	 * @param marketstatManager
	 *            the manager for statistic requests
	 * @param depthOfBookManager
	 *            the manager for market depth requests
	 * @throws IllegalArgumentException
	 *             if any parameter is null
	 */
	@Inject
	public MarketData(ILatestTickManager latestTickManager, ITopOfBookManager topOfBookManager, IMarketstatManager marketstatManager, IDepthOfBookManager depthOfBookManager) {
		Validate.noNullElements(new Object[] { latestTickManager, topOfBookManager, marketstatManager, depthOfBookManager });
		mLatestTickManager = latestTickManager;
		mTopOfBookManager = topOfBookManager;
		mMarketstatManager = marketstatManager;
		mDepthOfBookManager = depthOfBookManager;
	}

	/**
	 * Sets the source for all data.
	 * 
	 * @param module
	 *            the source module
	 * @throws IllegalArgumentException
	 *             if module is null
	 */
	public void setSourceModule(ModuleURN module) {
		setLatestTickSourceModule(module);
		setTopOfBookSourceModule(module);
		setMarketstatSourceModule(module);
		setDepthOfBookSourceModule(module);
	}

	/**
	 * Sets the source for the latest tick data.
	 * 
	 * @param module
	 *            the source module
	 * @throws IllegalArgumentException
	 *             if module is null
	 */
	public void setLatestTickSourceModule(ModuleURN module) {
		mLatestTickManager.setSourceModule(module);
	}

	/**
	 * Sets the source for the top of book data.
	 * 
	 * @param module
	 *            the source module
	 * @throws IllegalArgumentException
	 *             if module is null
	 */
	public void setTopOfBookSourceModule(ModuleURN module) {
		mTopOfBookManager.setSourceModule(module);
	}

	/**
	 * Sets the source for the market statistic data.
	 * 
	 * @param module
	 *            the source module
	 * @throws IllegalArgumentException
	 *             if module is null
	 */
	public void setMarketstatSourceModule(ModuleURN module) {
		mMarketstatManager.setSourceModule(module);
	}

	/**
	 * Sets the source for the market depth data.
	 * 
	 * @param module
	 *            the source module
	 * @throws IllegalArgumentException
	 *             if module is null
	 */
	public void setDepthOfBookSourceModule(ModuleURN module) {
		mDepthOfBookManager.setSourceModule(module);
	}

	@Override
	public synchronized IMarketDataReference<MDLatestTick> getLatestTick(String symbol) {
		Validate.notNull(symbol);
		synchronized (mLatestTickManager) {
			return new Reference<MDLatestTick, LatestTickKey>(mLatestTickManager,
					new LatestTickKey(symbol));
		}
	}

	@Override
	public synchronized IMarketDataReference<MDTopOfBook> getTopOfBook(String symbol) {
		Validate.notNull(symbol);
		synchronized (mTopOfBookManager) {
			return new Reference<MDTopOfBook, TopOfBookKey>(mTopOfBookManager, new TopOfBookKey(
					symbol));
		}
	}

	@Override
	public IMarketDataReference<MDMarketstat> getMarketstat(String symbol) {
		Validate.notNull(symbol);
		synchronized (mMarketstatManager) {
			return new Reference<MDMarketstat, MarketstatKey>(mMarketstatManager, new MarketstatKey(
					symbol));
		}
	}
	
	@Override
	public IMarketDataReference<MDDepthOfBook> getDepthOfBook(String symbol, Content product) {
		Validate.noNullElements(new Object[] {symbol, product});
		Validate.isTrue(DepthOfBookKey.VALID_PRODUCTS.contains(product));
		synchronized (mDepthOfBookManager) {
			return new Reference<MDDepthOfBook, DepthOfBookKey>(mDepthOfBookManager, new DepthOfBookKey(
					symbol, product));
		}
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
			if (!mReferences.contains(key)) {
				mManager.startFlow(key);
			}
			mReferences.add(key);
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
			synchronized (mManager) {
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
