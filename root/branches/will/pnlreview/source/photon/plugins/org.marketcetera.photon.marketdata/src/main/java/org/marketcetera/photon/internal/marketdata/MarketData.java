package org.marketcetera.photon.internal.marketdata;

import java.util.concurrent.atomic.AtomicBoolean;

import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.marketdata.IMarketData;
import org.marketcetera.photon.marketdata.IMarketDataReference;
import org.marketcetera.photon.model.marketdata.MDItem;
import org.marketcetera.photon.model.marketdata.MDLatestTick;
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
 * @since $Release$
 */
@ClassVersion("$Id$")
public class MarketData implements IMarketData {

	/*
	 * Thread safety is provided by synchronizing access to the individual managers.
	 */

	private final Multiset<Key<? extends MDItem>> mReferences = HashMultiset.create();
	private final ILatestTickManager mLatestTickManager;
	private final ITopOfBookManager mTopOfBookManager;

	@Inject
	public MarketData(ILatestTickManager latestTickManager, ITopOfBookManager topOfBookManager) {
		mLatestTickManager = latestTickManager;
		mTopOfBookManager = topOfBookManager;
	}

	/**
	 * @param module
	 */
	public void setSourceModule(ModuleURN module) {
		mLatestTickManager.setSourceModule(module);
		mTopOfBookManager.setSourceModule(module);
	}

	@Override
	public synchronized IMarketDataReference<MDLatestTick> getLatestTick(String symbol) {
		synchronized (mLatestTickManager) {
			return new Reference<MDLatestTick, LatestTickKey>(mLatestTickManager,
					new LatestTickKey(symbol));
		}
	}

	@Override
	public synchronized IMarketDataReference<MDTopOfBook> getTopOfBook(String symbol) {
		synchronized (mTopOfBookManager) {
			return new Reference<MDTopOfBook, TopOfBookKey>(mTopOfBookManager, new TopOfBookKey(
					symbol));
		}
	}

	/**
	 * A reference to a market data item.
	 */
	@ClassVersion("$Id$")
	private class Reference<T extends MDItem, K extends Key<T>> implements IMarketDataReference<T> {

		private final IDataFlowManager<T, K> mManager;
		private final T mItem;
		private final K mKey;
		private AtomicBoolean mDisposed = new AtomicBoolean();

		public Reference(IDataFlowManager<T, K> manager, K key) {
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
					throw new IllegalStateException();
				}
				if (!mReferences.contains(mKey)) {
					mManager.stopFlow(mKey);
				}
			}
		}
	}
}
