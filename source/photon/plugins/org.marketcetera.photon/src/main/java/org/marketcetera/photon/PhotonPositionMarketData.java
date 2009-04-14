package org.marketcetera.photon;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.marketcetera.core.position.MarketDataSupport;
import org.marketcetera.photon.marketdata.IMarketData;
import org.marketcetera.photon.marketdata.IMarketDataReference;
import org.marketcetera.photon.model.marketdata.MDLatestTick;
import org.marketcetera.photon.model.marketdata.MDPackage;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;

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

	private final Map<String, IMarketDataReference<MDLatestTick>> mLatestTickReferences = Maps
			.newHashMap();
	private final SetMultimap<MDLatestTick, SymbolChangeListener> mListeners = HashMultimap
			.create();
	private final IMarketData mMarketData;
	private final Adapter mLatestTickAdapter = new LatestTickAdapter();

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
	public BigDecimal getClosingPrice(String symbol) {
		Validate.notNull(symbol);
		return null;
	}

	@Override
	public BigDecimal getLastTradePrice(String symbol) {
		Validate.notNull(symbol);
		// implementation choice to only return the last trade price if it's already known
		// not worth it to set up a new data flow
		IMarketDataReference<MDLatestTick> ref = mLatestTickReferences.get(symbol);
		return ref == null ? null : ref.get().getPrice();
	}

	@Override
	public void addSymbolChangeListener(String symbol, SymbolChangeListener listener) {
		Validate.noNullElements(new Object[] {symbol, listener});
		IMarketDataReference<MDLatestTick> ref = mLatestTickReferences.get(symbol);
		if (ref == null) {
			ref = mMarketData.getLatestTick(symbol);
			mLatestTickReferences.put(symbol, ref);
			ref.get().eAdapters().add(mLatestTickAdapter);
		}
		MDLatestTick tick = ref.get();
		mListeners.put(tick, listener);
	}

	@Override
	public void removeSymbolChangeListener(String symbol, SymbolChangeListener listener) {
		Validate.noNullElements(new Object[] {symbol, listener});
		IMarketDataReference<MDLatestTick> ref = mLatestTickReferences.get(symbol);
		if (ref == null) {
			// no data for the symbol
			return;
		}
		MDLatestTick tick = ref.get();
		Set<SymbolChangeListener> listeners = mListeners.get(tick);
		listeners.remove(listener);
		if (listeners.isEmpty()) {
			tick.eAdapters().remove(mLatestTickAdapter);
			mLatestTickReferences.remove(symbol);
			ref.dispose();
		}
	}

	private class LatestTickAdapter extends AdapterImpl {

		@Override
		public void notifyChanged(Notification msg) {
			if (!msg.isTouch() && msg.getFeature() == MDPackage.Literals.MD_LATEST_TICK__PRICE) {
				SymbolChangeEvent event = new SymbolChangeEvent(PhotonPositionMarketData.this,
						(BigDecimal) msg.getNewValue());
				for (SymbolChangeListener listener : mListeners.get((MDLatestTick) msg
						.getNotifier())) {
					listener.symbolChanged(event);
				}
			}
		}
	}
}
