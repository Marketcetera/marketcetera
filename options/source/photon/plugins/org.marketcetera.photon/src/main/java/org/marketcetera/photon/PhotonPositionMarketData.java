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
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
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
	private final SetMultimap<Instrument, InstrumentMarketDataListener> mListeners = HashMultimap.create();
	private final Map<Instrument, IMarketDataReference<MDLatestTick>> mLatestTickReferences = Maps
			.newHashMap();
	private final Map<Instrument, IMarketDataReference<MDMarketstat>> mStatReferences = Maps
			.newHashMap();
    /*
     * These caches allow easy implementation of getLastTradePrice,
     * getClosingPrice, and getOptionMultiplier. They also allow notification to
     * be fired only when the values change to avoid unnecessary notifications,
     * which is especially important with closing price and option multiplier
     * that rarely change.
     */
    private final ConcurrentMap<Instrument, BigDecimal> mLatestTickCache = new ConcurrentHashMap<Instrument, BigDecimal>();
    private final ConcurrentMap<Instrument, BigDecimal> mClosingPriceCache = new ConcurrentHashMap<Instrument, BigDecimal>();
    private final ConcurrentMap<Instrument, Integer> mOptionMultiplierCache = new ConcurrentHashMap<Instrument, Integer>();

	/*
	 * Marks null price for the ConcurrentMap caches which don't allow null. This is better than
	 * removing keys since it allows the concurrent put method to be used in {@link
	 * #fireIfChanged(String, BigDecimal, ConcurrentMap, boolean)}
	 */
	private static final BigDecimal NULL_BIG_DECIMAL = new BigDecimal(Integer.MIN_VALUE);
	private static final Integer NULL_INTEGER = Integer.MIN_VALUE;

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
	public BigDecimal getLastTradePrice(Instrument instrument) {
		Validate.notNull(instrument);
		// implementation choice to only return the last trade price if it's already known
		// not worth it to set up a new data flow
		return getCachedValue(mLatestTickCache, instrument, NULL_BIG_DECIMAL);
	}

	@Override
	public BigDecimal getClosingPrice(Instrument instrument) {
		Validate.notNull(instrument);
		// implementation choice to only return the closing price if it's already known
		// not worth it to set up a new data flow
		return getCachedValue(mClosingPriceCache, instrument, NULL_BIG_DECIMAL);
	}
	
	@Override
	public Integer getOptionMultiplier(Option option) {
	    Validate.notNull(option);
        // implementation choice to only return the multiplier if it's already known
        // not worth it to set up a new data flow
        return getCachedValue(mOptionMultiplierCache, option, NULL_INTEGER);
	}

	private <T> T getCachedValue(final ConcurrentMap<Instrument, T> cache,
			final Instrument symbol, T nullObject) {
		T cached = cache.get(symbol);
		return cached == nullObject ? null : cached;
	}

	@Override
	public void addInstrumentMarketDataListener(Instrument instrument, InstrumentMarketDataListener listener) {
		Validate.noNullElements(new Object[] { instrument, listener });
		synchronized (mListeners) {
			if (mDisposed.get()) return;
			IMarketDataReference<MDLatestTick> ref = mLatestTickReferences.get(instrument);
			if (ref == null) {
				ref = mMarketData.getLatestTick(instrument);
				mLatestTickReferences.put(instrument, ref);
				ref.get().eAdapters().add(mLatestTickAdapter);
			}
			IMarketDataReference<MDMarketstat> statRef = mStatReferences.get(instrument);
			if (statRef == null) {
				statRef = mMarketData.getMarketstat(instrument);
				mStatReferences.put(instrument, statRef);
				statRef.get().eAdapters().add(mClosingPriceAdapter);
			}
			mListeners.put(instrument, listener);
		}
	}

	@Override
	public void removeInstrumentMarketDataListener(Instrument instrument, InstrumentMarketDataListener listener) {
		Validate.noNullElements(new Object[] { instrument, listener });
		List<IMarketDataReference<?>> toDispose = Lists.newArrayList();
		synchronized (mListeners) {
			IMarketDataReference<MDLatestTick> ref = mLatestTickReferences.get(instrument);
			IMarketDataReference<MDMarketstat> statRef = mStatReferences.get(instrument);
			Set<InstrumentMarketDataListener> listeners = mListeners.get(instrument);
			listeners.remove(listener);
			if (listeners.isEmpty()) {
				if (ref != null) {
					MDLatestTick tick = ref.get();
					if (tick != null) {
						tick.eAdapters().remove(mLatestTickAdapter);
						mLatestTickReferences.remove(instrument);
						mLatestTickCache.remove(instrument);
						toDispose.add(ref);
					}
				}
				if (statRef != null) {
					MDMarketstat stat = statRef.get();
					if (stat != null) {
						stat.eAdapters().remove(mClosingPriceAdapter);
						mStatReferences.remove(instrument);
						mClosingPriceCache.remove(instrument);
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
	    Instrument instrument = item.getInstrument();
        BigDecimal newValue = item.getPrice();
        if (updateCache(instrument, newValue, mLatestTickCache, NULL_BIG_DECIMAL)) {
	        InstrumentMarketDataEvent event = new InstrumentMarketDataEvent(this, newValue);
	        synchronized (mListeners) {
	            if (mDisposed.get()) return;
	            for (InstrumentMarketDataListener listener : mListeners.get(instrument)) {
	               listener.symbolTraded(event);
	            }
	        }
	    }
	}

	private void fireClosingPriceChange(final MDMarketstat item) {
	    Instrument instrument = item.getInstrument();
        BigDecimal newValue = item.getPreviousClosePrice();
        if (updateCache(instrument, newValue, mClosingPriceCache, NULL_BIG_DECIMAL)) {
            InstrumentMarketDataEvent event = new InstrumentMarketDataEvent(this, newValue);
            synchronized (mListeners) {
                if (mDisposed.get()) return;
                for (InstrumentMarketDataListener listener : mListeners.get(instrument)) {
                   listener.closePriceChanged(event);
                }
            }
        }
	}

	private void fireMultiplierChanged(final MDLatestTick item) {
	    Instrument instrument = item.getInstrument();
        Integer newValue = item.getMultiplier();
        if (updateCache(instrument, newValue, mOptionMultiplierCache, NULL_INTEGER)) {
            synchronized (mListeners) {
                if (mDisposed.get()) return;
                for (InstrumentMarketDataListener listener : mListeners.get(instrument)) {
                   listener.optionMultiplierChanged(newValue);
                }
            }
        }
    }
	
	/**
	 * Updates an internal cache and returns whether the value changed.
	 */
	private <T extends Comparable<T>> boolean updateCache(final Instrument instrument, T newValue,
            final ConcurrentMap<Instrument, T> cache, T nullObject) {
        T oldValue = cache.put(instrument, newValue == null ? nullObject : newValue);
        if (oldValue == nullObject) {
            oldValue = null;
        }
        // only notify if the value changed
        if (oldValue == null && newValue == null) {
            return false;
        } else if (oldValue != null && newValue != null && oldValue.compareTo(newValue) == 0) {
            return false;
        }
        return true;
    }

	@Override
	public void dispose() {
		if (mDisposed.compareAndSet(false, true)) {
			Set<Map.Entry<Instrument, InstrumentMarketDataListener>> entries;
			synchronized (mListeners) {
				// make a copy since we will be modifying mListeners
				entries = Sets.newHashSet(mListeners
						.entries());
			}
			for (Map.Entry<Instrument, InstrumentMarketDataListener> entry : entries) {
				removeInstrumentMarketDataListener(entry.getKey(), entry.getValue());
			}
		}
	}

	private class LatestTickAdapter extends AdapterImpl {

		@Override
		public void notifyChanged(Notification msg) {
			if (!msg.isTouch() && msg.getEventType() == Notification.SET) {
			    MDLatestTick item = (MDLatestTick) msg.getNotifier();
                if (msg.getFeature() == MDPackage.Literals.MD_LATEST_TICK__PRICE) {
                    fireSymbolTraded(item);
                } else if (msg.getFeature() == MDPackage.Literals.MD_LATEST_TICK__MULTIPLIER) {
                    fireMultiplierChanged(item);
                }
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
