package org.marketcetera.photon;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.Validate;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EAttribute;
import org.marketcetera.core.position.MarketDataSupport;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.photon.marketdata.IMarketData;
import org.marketcetera.photon.marketdata.IMarketDataReference;
import org.marketcetera.photon.marketdata.MarketDataEventBus;
import org.marketcetera.photon.model.marketdata.MDLatestTick;
import org.marketcetera.photon.model.marketdata.MDMarketstat;
import org.marketcetera.photon.model.marketdata.MDPackage;
import org.marketcetera.photon.model.marketdata.MDTopOfBook;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;

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
	private final Map<Instrument, IMarketDataReference<MDLatestTick>> mLatestTickReferences = Maps.newHashMap();
	private final Map<Instrument, IMarketDataReference<MDMarketstat>> mStatReferences = Maps.newHashMap();
    private final Map<Instrument, IMarketDataReference<MDTopOfBook>> mTopOfBookReferences = Maps.newHashMap();
    /*
     * These caches allow easy implementation of getLastTradePrice,
     * getClosingPrice, getOptionMultiplier and getFutureMultiplier. They also allow notification to
     * be fired only when the values change to avoid unnecessary notifications,
     * which is especially important with closing price and option multiplier
     * that rarely change.
     */
    private final Map<Instrument, BigDecimal> mLatestTickCache = new ConcurrentHashMap<Instrument, BigDecimal>();
    private final Map<Instrument, BigDecimal> mClosingPriceCache = new ConcurrentHashMap<Instrument, BigDecimal>();
    private final Map<Instrument, BigDecimal> mOptionMultiplierCache = new ConcurrentHashMap<Instrument, BigDecimal>();
    private final Map<Instrument, BigDecimal> mFutureMultiplierCache = new ConcurrentHashMap<Instrument, BigDecimal>();
    private final Map<Instrument, BigDecimal> mBidCache = new ConcurrentHashMap<Instrument,BigDecimal>();
    private final Map<Instrument, BigDecimal> mAskCache = new ConcurrentHashMap<Instrument,BigDecimal>();
	/*
	 * Marks null price for the ConcurrentMap caches which don't allow null. This is better than
	 * removing keys since it allows the concurrent put method to be used in {@link
	 * #fireIfChanged(String, BigDecimal, ConcurrentMap, boolean)}
	 */
	private static final BigDecimal NULL = new BigDecimal(Integer.MIN_VALUE);

    /**
     * Constructor.
     * 
     * @param inMarketData the market data provider
     * @throws IllegalArgumentException if marketData is null
     */
    public PhotonPositionMarketData(IMarketData inMarketData)
    {
        Validate.notNull(inMarketData);
        mMarketData = inMarketData;
        MarketDataEventBus.register(this);
    }
    /**
     * Receive an updated market stat event.
     *
     * @param inEvent a <code>MarketstatEvent</code> value
     */
    @Subscribe
    public void receiveMarketStat(MarketstatEvent inEvent)
    {
        Instrument instrument = inEvent.getInstrument();
        BigDecimal newValue = inEvent.getClose();
        updateCache(instrument, newValue, mClosingPriceCache);
        InstrumentMarketDataEvent event = new InstrumentMarketDataEvent(this, newValue);
        synchronized (mListeners) {
            if (mDisposed.get()) return;
            for (InstrumentMarketDataListener listener : mListeners.get(instrument)) {
                listener.closePriceChanged(event);
            }
        }
    }
    /**
     * Receive a trade event.
     *
     * @param inEvent a <code>TradeEvent</code> value
     */
    @Subscribe
    public void receiveTrade(TradeEvent inEvent)
    {
        Instrument instrument = inEvent.getInstrument();
        BigDecimal newValue = inEvent.getPrice();
        updateCache(instrument,
                    newValue,
                    mLatestTickCache);
        InstrumentMarketDataEvent event = new InstrumentMarketDataEvent(this,
                                                                        newValue);
        synchronized (mListeners) {
            if (mDisposed.get()) return;
            for(InstrumentMarketDataListener listener : mListeners.get(instrument)) {
                listener.symbolTraded(event);
            }
        }
    }
    /**
     * Receive a quote event.
     *
     * @param inEvent a <code>QuoteEvent</code> value
     */
    @Subscribe
    public void receiveQuote(QuoteEvent inEvent)
    {
        Instrument instrument = inEvent.getInstrument();
        BigDecimal newValue = inEvent.getPrice();
        Map<Instrument,BigDecimal> quoteCache = inEvent instanceof BidEvent ? mBidCache : mAskCache;
        updateCache(instrument,
                    newValue,
                    quoteCache);
        InstrumentMarketDataEvent event = new InstrumentMarketDataEvent(this,
                                                                        newValue);
        synchronized(mListeners) {
            if(mDisposed.get()) {
                return;
            }
            for(InstrumentMarketDataListener listener : mListeners.get(instrument)) {
                if(inEvent instanceof BidEvent) {
                    listener.bidChanged(event);
                } else {
                    listener.askChanged(event);
                }
            }
        }
    }
	@Override
	public BigDecimal getLastTradePrice(Instrument instrument) {
	    Validate.notNull(instrument);
	    // implementation choice to only return the last trade price if it's already known
	    // not worth it to set up a new data flow
	    return getCachedValue(mLatestTickCache, instrument);
	}

	@Override
	public BigDecimal getClosingPrice(Instrument instrument) {
		Validate.notNull(instrument);
		// implementation choice to only return the closing price if it's already known
		// not worth it to set up a new data flow
		return getCachedValue(mClosingPriceCache, instrument);
	}
	
	@Override
	public BigDecimal getOptionMultiplier(Option option) {
	    Validate.notNull(option);
        // implementation choice to only return the multiplier if it's already known
        // not worth it to set up a new data flow
        return getCachedValue(mOptionMultiplierCache, option);
	}
	
	@Override
	public BigDecimal getFutureMultiplier(Future future) {
	    Validate.notNull(future);
        // implementation choice to only return the multiplier if it's already known
        // not worth it to set up a new data flow
        return getCachedValue(mFutureMultiplierCache, future);
	}
    @Override
    public void addInstrumentMarketDataListener(Instrument inInstrument,
                                                InstrumentMarketDataListener inListener)
    {
        Validate.noNullElements(new Object[] { inInstrument, inListener });
        synchronized(mListeners) {
            if(mDisposed.get()) return;
            IMarketDataReference<MDLatestTick> ref = mLatestTickReferences.get(inInstrument);
            if(ref == null) {
                ref = mMarketData.getLatestTick(inInstrument);
                if(ref != null && ref.get() != null) {
                    mLatestTickReferences.put(inInstrument,
                                              ref);
                    ref.get().eAdapters().add(mLatestTickAdapter);
                }
            }
            IMarketDataReference<MDMarketstat> statRef = mStatReferences.get(inInstrument);
            if(statRef == null) {
                statRef = mMarketData.getMarketstat(inInstrument);
                mStatReferences.put(inInstrument,
                                    statRef);
                if(statRef != null && statRef.get() != null) {
                    statRef.get().eAdapters().add(mClosingPriceAdapter);
                }
            }
            IMarketDataReference<MDTopOfBook> topOfBookRef = mMarketData.getTopOfBook(inInstrument);
            if(topOfBookRef == null) {
                topOfBookRef = mMarketData.getTopOfBook(inInstrument);
                if(topOfBookRef != null && topOfBookRef.get() != null) {
                    mTopOfBookReferences.put(inInstrument,
                                             topOfBookRef);
                }
            }
            mListeners.put(inInstrument, inListener);
        }
    }

    @Override
    public void removeInstrumentMarketDataListener(Instrument instrument,
                                                   InstrumentMarketDataListener listener)
    {
        Validate.noNullElements(new Object[] { instrument, listener });
        List<IMarketDataReference<?>> toDispose = Lists.newArrayList();
        synchronized (mListeners) {
            IMarketDataReference<MDLatestTick> ref = mLatestTickReferences.get(instrument);
            IMarketDataReference<MDMarketstat> statRef = mStatReferences.get(instrument);
            IMarketDataReference<MDTopOfBook> topOfBookRef = mTopOfBookReferences.get(instrument);
            Set<InstrumentMarketDataListener> listeners = mListeners.get(instrument);
            listeners.remove(listener);
            if(listeners.isEmpty()) {
                if(ref != null) {
                    MDLatestTick tick = ref.get();
                    if(tick != null) {
                        tick.eAdapters().remove(mLatestTickAdapter);
                        mLatestTickReferences.remove(instrument);
                        mLatestTickCache.remove(instrument);
                        toDispose.add(ref);
                    }
                }
                if(statRef != null) {
                    MDMarketstat stat = statRef.get();
                    if(stat != null) {
                        stat.eAdapters().remove(mClosingPriceAdapter);
                        mStatReferences.remove(instrument);
                        mClosingPriceCache.remove(instrument);
                        toDispose.add(statRef);
                    }
                }
                if(topOfBookRef != null) {
                    MDTopOfBook topOfBook = topOfBookRef.get();
                    if(topOfBook != null) {
                        mTopOfBookReferences.remove(instrument);
                        toDispose.add(topOfBookRef);
                    }
                }
            }
        }
        // dispose outside of the lock to avoid deadlock
        for(IMarketDataReference<?> ref : toDispose) {
            ref.dispose();
        }
    }
    /**
     * Get the cached value for the given instrument from the given cache.
     *
     * @param inCache a <code>Map&lt;Instrument,BigDecimal&gt;</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @return a <code>BigDecimal</code> value or <code>null</code>
     */
    private BigDecimal getCachedValue(Map<Instrument,BigDecimal> inCache,
                                      Instrument inInstrument) {
        BigDecimal cached = inCache.get(inInstrument);
        return cached == NULL ? null : cached;
    }
	private void fireSymbolTraded(final MDLatestTick item) {
	    Instrument instrument = item.getInstrument();
        BigDecimal newValue = item.getPrice();
        if (updateCache(instrument, newValue, mLatestTickCache)) {
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
        if (updateCache(instrument, newValue, mClosingPriceCache)) {
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
        BigDecimal newValue = item.getMultiplier();
        if(item.getInstrument().getSecurityType()==SecurityType.Option){
	        if (updateCache(instrument, newValue, mOptionMultiplierCache)) {
	            InstrumentMarketDataEvent event = new InstrumentMarketDataEvent(this, newValue);
	            synchronized (mListeners) {
	                if (mDisposed.get()) return;
	                for (InstrumentMarketDataListener listener : mListeners.get(instrument)) {
	                   listener.optionMultiplierChanged(event);
	                }
	            }
	        }
        }
        if(item.getInstrument().getSecurityType()==SecurityType.Future){
	        if (updateCache(instrument, newValue, mFutureMultiplierCache)) {
	            InstrumentMarketDataEvent event = new InstrumentMarketDataEvent(this, newValue);
	            synchronized (mListeners) {
	                if (mDisposed.get()) return;
	                for (InstrumentMarketDataListener listener : mListeners.get(instrument)) {
	                   listener.futureMultiplierChanged(event);
	                }
	            }
	        }
        }
    }
    /**
     * Updates an internal cache and returns whether the value changed.
     */
    private boolean updateCache(final Instrument instrument,
                                BigDecimal newValue,
                                Map<Instrument,BigDecimal> cache)
    {
        BigDecimal oldValue = cache.put(instrument,
                                        newValue == null ? NULL : newValue);
        if(oldValue == NULL) {
            oldValue = null;
        }
        // only notify if the value changed
        if(oldValue == null && newValue == null) {
            return false;
        } else if (oldValue != null && newValue != null && oldValue.compareTo(newValue) == 0) {
            return false;
        }
        return true;
    }

    @Override
    public void dispose()
    {
        MarketDataEventBus.unregister(this);
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
