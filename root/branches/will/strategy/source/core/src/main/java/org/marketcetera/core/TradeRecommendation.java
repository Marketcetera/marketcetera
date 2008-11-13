package org.marketcetera.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;

import org.marketcetera.messagehistory.MessageHolder;

import quickfix.Message;

public class TradeRecommendation extends MessageHolder {

	private Date createdAt;
	private Double score;
	
	public TradeRecommendation(Message message, Double score) {
		super(message);
		createdAt = new Date(); //i18n_datetime
		this.score = score;
	}
	

	public Double getScore() {
		return score;
	}


	public Date getCreationDate(){
		return createdAt;
	}
	
	public String getStringRepresentation(){
		return toString();
	}
	
	@Override
	public String toString() {
		return "Trade: " + getMessage(); //$NON-NLS-1$
	}
	
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}

}
