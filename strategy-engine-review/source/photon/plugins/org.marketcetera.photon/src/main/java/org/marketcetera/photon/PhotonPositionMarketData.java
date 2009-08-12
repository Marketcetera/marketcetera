package org.marketcetera.photon;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.Validate;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EAttribute;
import org.marketcetera.core.position.MarketDataSupport;
import org.marketcetera.photon.marketdata.IMarketData;
import org.marketcetera.photon.marketdata.IMarketDataReference;
import org.marketcetera.photon.model.marketdata.MDLatestTick;
import org.marketcetera.photon.model.marketdata.MDMarketstat;
import org.marketcetera.photon.model.marketdata.MDPackage;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

/* $License$ */

/**
 * Implements MarketDataSupport for the position engine in Photon. Market data is provided by the
 * common marketdata infrastructure in {@link IMarketData}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class PhotonPositionMarketData implements MarketDataSupport {

	private final IMarketData mMarketData;
	private final AtomicBoolean mDisposed = new AtomicBoolean();
	private final Adapter mLatestTickAdapter = new LatestTickAdapter();
	private final Adapter mClosingPriceAdapter = new ClosingPriceAdapter();

	/*
	 * mListeners synchronizes access to the following three collections.
	 */
	private final SetMultimap<String, SymbolChangeListener> mListeners = HashMultimap.create();
	private final Map<String, IMarketDataReference<MDLatestTick>> mLatestTickReferences = Maps
			.newHashMap();
	private final Map<String, IMarketDataReference<MDMarketstat>> mStatReferences = Maps
			.newHashMap();
	/*
	 * These caches allow easy implementation of getLastTradePrice and getClosingPrice. They also
	 * allow notification to be fired only when the values change to avoid unnecessary
	 * notifications, which is especially important with closing price that rarely changes.
	 */
	private final ConcurrentMap<String, BigDecimal> mLatestTickCache = new ConcurrentHashMap<String, BigDecimal>();
	private final ConcurrentMap<String, BigDecimal> mClosingPriceCache = new ConcurrentHashMap<String, BigDecimal>();

	/*
	 * Marks null price for the ConcurrentMap caches which don't allow null. This is better than
	 * removing keys since it allows the concurrent put method to be used in {@link
	 * #fireIfChanged(String, BigDecimal, ConcurrentMap, boolean)}
	 */
	private static final BigDecimal NULL = new BigDecimal(Integer.MIN_VALUE);

	/**
	 * Constructor.
	 * 
	 * @param marketData
	 *            the market data provider
	 * @throws IllegalArgumentException
	 *             if marketData is null
	 */
	public PhotonPositionMarketData(IMarketData marketData) {
		Validate.notNull(marketData);
		mMarketData = marketData;
	}

	@Override
	public BigDecimal getLastTradePrice(String symbol) {
		Validate.notNull(symbol);
		// implementation choice to only return the last trade price if it's already known
		// not worth it to set up a new data flow
		return getCachedValue(mLatestTickCache, symbol);
	}

	@Override
	public BigDecimal getClosingPrice(String symbol) {
		Validate.notNull(symbol);
		// implementation choice to only return the closing price if it's already known
		// not worth it to set up a new data flow
		return getCachedValue(mClosingPriceCache, symbol);
	}

	private BigDecimal getCachedValue(final ConcurrentMap<String, BigDecimal> cache,
			final String symbol) {
		BigDecimal cached = cache.get(symbol);
		return cached == NULL ? null : cached;
	}

	@Override
	public void addSymbolChangeListener(String symbol, SymbolChangeListener listener) {
		Validate.noNullElements(new Object[] { symbol, listener });
		synchronized (mListeners) {
			if (mDisposed.get()) return;
			IMarketDataReference<MDLatestTick> ref = mLatestTickReferences.get(symbol);
			if (ref == null) {
				ref = mMarketData.getLatestTick(symbol);
				mLatestTickReferences.put(symbol, ref);
				ref.get().eAdapters().add(mLatestTickAdapter);
			}
			IMarketDataReference<MDMarketstat> statRef = mStatReferences.get(symbol);
			if (statRef == null) {
				statRef = mMarketData.getMarketstat(symbol);
				mStatReferences.put(symbol, statRef);
				statRef.get().eAdapters().add(mClosingPriceAdapter);
			}
			mListeners.put(symbol, listener);
		}
	}

	@Override
	public void removeSymbolChangeListener(String symbol, SymbolChangeListener listener) {
		Validate.noNullElements(new Object[] { symbol, listener });
		List<IMarketDataReference<?>> toDispose = Lists.newArrayList();
		synchronized (mListeners) {
			IMarketDataReference<MDLatestTick> ref = mLatestTickReferences.get(symbol);
			IMarketDataReference<MDMarketstat> statRef = mStatReferences.get(symbol);
			Set<SymbolChangeListener> listeners = mListeners.get(symbol);
			listeners.remove(listener);
			if (listeners.isEmpty()) {
				if (ref != null) {
					MDLatestTick tick = ref.get();
					if (tick != null) {
						tick.eAdapters().remove(mLatestTickAdapter);
						mLatestTickReferences.remove(symbol);
						mLatestTickCache.remove(symbol);
						toDispose.add(ref);
					}
				}
				if (statRef != null) {
					MDMarketstat stat = statRef.get();
					if (stat != null) {
						stat.eAdapters().remove(mClosingPriceAdapter);
						mStatReferences.remove(symbol);
						mClosingPriceCache.remove(symbol);
						toDispose.add(statRef);
					}
				}
			}
		}
		// dispose outside of the lock to avoid deadlock
		for (IMarketDataReference<?> ref : toDispose) {
			ref.dispose();
		}
	}

	private void fireSymbolTraded(final MDLatestTick item) {
		fireIfChanged(item.getSymbol(), item.getPrice(), mLatestTickCache, true);
	}

	private void fireClosingPriceChange(final MDMarketstat item) {
		fireIfChanged(item.getSymbol(), item.getPreviousClosePrice(), mClosingPriceCache, false);
	}

	private void fireIfChanged(final String symbol, BigDecimal newPrice,
			final ConcurrentMap<String, BigDecimal> cache,
			final boolean trueForSymbolTradeFalseForClosePrice) {
		BigDecimal oldPrice = cache.put(symbol, newPrice == null ? NULL : newPrice);
		if (oldPrice == NULL) {
			oldPrice = null;
		}
		// only notify if the value changed
		if (oldPrice == null && newPrice == null) {
			return;
		} else if (oldPrice != null && newPrice != null && oldPrice.compareTo(newPrice) == 0) {
			return;
		}
		SymbolChangeEvent event = new SymbolChangeEvent(PhotonPositionMarketData.this, newPrice);
		synchronized (mListeners) {
			if (mDisposed.get()) return;
			for (SymbolChangeListener listener : mListeners.get(symbol)) {
				if (trueForSymbolTradeFalseForClosePrice) {
					listener.symbolTraded(event);
				} else {
					listener.closePriceChanged(event);
				}
			}
		}
	}

	@Override
	public void dispose() {
		if (mDisposed.compareAndSet(false, true)) {
			Set<Map.Entry<String, SymbolChangeListener>> entries;
			synchronized (mListeners) {
				// make a copy since we will be modifying mListeners
				entries = Sets.newHashSet(mListeners
						.entries());
			}
			for (Map.Entry<String, SymbolChangeListener> entry : entries) {
				removeSymbolChangeListener(entry.getKey(), entry.getValue());
			}
		}
	}

	private class LatestTickAdapter extends AdapterImpl {

		@Override
		public void notifyChanged(Notification msg) {
			if (!msg.isTouch() && msg.getEventType() == Notification.SET
					&& msg.getFeature() == MDPackage.Literals.MD_LATEST_TICK__PRICE) {
				MDLatestTick item = (MDLatestTick) msg.getNotifier();
				fireSymbolTraded(item);
			}
		}
	}

	private class ClosingPriceAdapter extends AdapterImpl {

		private final ImmutableSet<EAttribute> mAttributes = ImmutableSet.of(
				MDPackage.Literals.MD_MARKETSTAT__CLOSE_DATE,
				MDPackage.Literals.MD_MARKETSTAT__CLOSE_PRICE,
				MDPackage.Literals.MD_MARKETSTAT__PREVIOUS_CLOSE_DATE,
				MDPackage.Literals.MD_MARKETSTAT__PREVIOUS_CLOSE_PRICE);

		@Override
		public void notifyChanged(Notification msg) {
			if (!msg.isTouch() && msg.getEventType() == Notification.SET
					&& mAttributes.contains(msg.getFeature())) {
				MDMarketstat item = (MDMarketstat) msg.getNotifier();
				fireClosingPriceChange(item);
			}
		}
	}
}
