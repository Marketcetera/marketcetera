package org.marketcetera.photon.views;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;

import org.marketcetera.core.MSymbol;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.TradeEvent;

public class MarketDataViewItem {
	MSymbol symbol;

	BidEvent bidEvent;
	AskEvent askEvent;
	TradeEvent tradeEvent;
	
	PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	public MarketDataViewItem(MSymbol symbol) {
		this.symbol = symbol;
	}
	
	public MSymbol getSymbol() {
		return symbol;
	}
	public BigDecimal getLastPx() {
		return tradeEvent == null ? null : tradeEvent.getPrice();
	}
	public BigDecimal getLastQty() {
		return tradeEvent == null ? null : tradeEvent.getSize();
	}
	public BigDecimal getBidSize() {
		return bidEvent == null ? null : bidEvent.getSize();
	}
	public BigDecimal getBidPx() {
		return bidEvent == null ? null : bidEvent.getPrice();
	}
	public BigDecimal getOfferPx() {
		return askEvent == null ? null : askEvent.getPrice();
	}
	public BigDecimal getOfferSize() {
		return askEvent == null ? null : askEvent.getSize();
	}
	
	
	public void setBidEvent(BidEvent bidEvent) {
		BigDecimal oldPxValue = getBidPx();
		BigDecimal oldSizeValue = getBidSize();
		this.bidEvent = bidEvent;
		propertyChangeSupport.firePropertyChange("bidPx", oldPxValue, getBidPx()); //$NON-NLS-1$
		propertyChangeSupport.firePropertyChange("bidSize", oldSizeValue, getBidSize()); //$NON-NLS-1$
	}
	
	public void setAskEvent(AskEvent askEvent) {
		BigDecimal oldPxValue = getOfferPx();
		BigDecimal oldSizeValue = getOfferSize();
		this.askEvent = askEvent;
		propertyChangeSupport.firePropertyChange("offerPx", oldPxValue, getOfferPx()); //$NON-NLS-1$
		propertyChangeSupport.firePropertyChange("offerSize", oldSizeValue, getOfferSize()); //$NON-NLS-1$
	}
	
	public void setTradeEvent(TradeEvent tradeEvent) {
		BigDecimal oldPxValue = getLastPx();
		BigDecimal oldSizeValue = getLastQty();
		this.tradeEvent = tradeEvent;
		propertyChangeSupport.firePropertyChange("bidPx", oldPxValue, getLastPx()); //$NON-NLS-1$
		propertyChangeSupport.firePropertyChange("bidSize", oldSizeValue, getLastQty()); //$NON-NLS-1$
	}
	
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}
	public PropertyChangeListener[] getPropertyChangeListeners(
			String propertyName) {
		return propertyChangeSupport.getPropertyChangeListeners(propertyName);
	}
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}

	
	
}
