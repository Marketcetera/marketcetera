package org.marketcetera.photon.views;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;
import java.text.MessageFormat;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.core.runtime.Assert;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.SymbolExchangeEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.ui.ISymbolProvider;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Market data view model object.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id")//$NON-NLS-1$
public class MarketDataViewItem implements ISymbolProvider {

	private static final MessageFormat FORMAT = new MessageFormat(
			"{0}\t{1}\t{2}\t{3}\t{4}\t{5}\t{6}"); //$NON-NLS-1$

	private MSymbol mSymbol;
	private BidEvent mBidEvent;
	private AskEvent mAskEvent;
	private TradeEvent mTradeEvent;

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	/**
	 * Constructor. Will throw an exception if symbol is null.
	 * 
	 * @param symbol
	 *            symbol for item, cannot be null
	 */
	public MarketDataViewItem(MSymbol symbol) {
		Assert.isNotNull(symbol);
		mSymbol = symbol;
	}

	@Override
	public MSymbol getSymbol() {
		return mSymbol;
	}

	/**
	 * @return the last trade price, or null if none available
	 */
	public BigDecimal getLastPx() {
		return mTradeEvent == null ? null : mTradeEvent.getPrice();
	}

	/**
	 * @return the last trade quantity, or null if none available
	 */
	public BigDecimal getLastQty() {
		return mTradeEvent == null ? null : mTradeEvent.getSize();
	}

	/**
	 * @return the last bid size, or null if none available
	 */
	public BigDecimal getBidSize() {
		return mBidEvent == null ? null : mBidEvent.getSize();
	}

	/**
	 * @return the last bid price, or null if none available
	 */
	public BigDecimal getBidPx() {
		return mBidEvent == null ? null : mBidEvent.getPrice();
	}

	/**
	 * @return the last offer price, or null if none available
	 */
	public BigDecimal getOfferPx() {
		return mAskEvent == null ? null : mAskEvent.getPrice();
	}

	/**
	 * @return the last offer size, or null if none available
	 */
	public BigDecimal getOfferSize() {
		return mAskEvent == null ? null : mAskEvent.getSize();
	}

	/**
	 * Changes the underlying symbol of this item. All data will be reset if the
	 * symbol changes. An exception will be thrown if symbol is null.
	 * 
	 * @param symbol
	 *            the new symbol
	 */
	public void setSymbol(MSymbol symbol) {
		Assert.isNotNull(symbol);
		MSymbol oldSymbol = getSymbol();
		if (!oldSymbol.equals(symbol)) {
			mSymbol = symbol;
			propertyChangeSupport.firePropertyChange(
					"symbol", oldSymbol, getSymbol()); //$NON-NLS-1$
			setBidEvent(null);
			setAskEvent(null);
			setTradeEvent(null);
		}
	}

	/**
	 * Update cached bid event.
	 * 
	 * @param bidEvent
	 *            the bid event
	 */
	public void setBidEvent(BidEvent bidEvent) {
		if (!ObjectUtils.equals(mBidEvent, bidEvent)) {
			if (!validateSymbol(bidEvent)) {
				return;
			}
			BigDecimal oldPxValue = getBidPx();
			BigDecimal oldSizeValue = getBidSize();
			mBidEvent = bidEvent;
			propertyChangeSupport.firePropertyChange(
					"bidPx", oldPxValue, getBidPx()); //$NON-NLS-1$
			propertyChangeSupport.firePropertyChange(
					"bidSize", oldSizeValue, getBidSize()); //$NON-NLS-1$
		}
	}

	/**
	 * Update cached ask event.
	 * 
	 * @param askEvent
	 *            the ask event
	 */
	public void setAskEvent(AskEvent askEvent) {
		if (!ObjectUtils.equals(mAskEvent, askEvent)) {
			if (!validateSymbol(askEvent)) {
				return;
			}
			BigDecimal oldPxValue = getOfferPx();
			BigDecimal oldSizeValue = getOfferSize();
			mAskEvent = askEvent;
			propertyChangeSupport.firePropertyChange(
					"offerPx", oldPxValue, getOfferPx()); //$NON-NLS-1$
			propertyChangeSupport.firePropertyChange(
					"offerSize", oldSizeValue, getOfferSize()); //$NON-NLS-1$
		}
	}

	/**
	 * Update cached trade event.
	 * 
	 * @param tradeEvent
	 *            the trade event
	 */
	public void setTradeEvent(TradeEvent tradeEvent) {
		if (!ObjectUtils.equals(mTradeEvent, tradeEvent)) {
			if (!validateSymbol(tradeEvent)) {
				return;
			}
			BigDecimal oldPxValue = getLastPx();
			BigDecimal oldSizeValue = getLastQty();
			mTradeEvent = tradeEvent;
			propertyChangeSupport.firePropertyChange(
					"lastPx", oldPxValue, getLastPx()); //$NON-NLS-1$
			propertyChangeSupport.firePropertyChange(
					"lastQty", oldSizeValue, getLastQty()); //$NON-NLS-1$
		}
	}

	private boolean validateSymbol(SymbolExchangeEvent event) {
		if (event != null) {
			final MSymbol newSymbol = new MSymbol(event.getSymbol());
			if (!getSymbol().equals(newSymbol)) {
				Messages.MARKET_DATA_EVENT_SYMBOL_MISMATCH.warn(this, getSymbol(),
						newSymbol);
				return false;
			}
		}
		return true;
	}

	/**
	 * @see PropertyChangeSupport#addPropertyChangeListener(String,
	 *      PropertyChangeListener)
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * @see PropertyChangeSupport#getPropertyChangeListeners(String)
	 */
	public PropertyChangeListener[] getPropertyChangeListeners(
			String propertyName) {
		return propertyChangeSupport.getPropertyChangeListeners(propertyName);
	}

	/**
	 * @see PropertyChangeSupport#removePropertyChangeListener(String,
	 *      PropertyChangeListener)
	 */
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}

	/**
	 * This implementation returns a string representation of the item's
	 * properties separated by tabs.
	 */
	@Override
	public String toString() {
		return FORMAT.format(new Object[] { getSymbol(), getLastPx(),
				getLastQty(), getBidSize(), getBidPx(), getOfferPx(),
				getOfferSize() });
	}
}
