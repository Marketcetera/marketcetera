package org.marketcetera.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;

import org.marketcetera.messagehistory.OutgoingMessageHolder;

import quickfix.Message;

public class TradeRecommendation extends OutgoingMessageHolder {

	private Date createdAt;
	private Double score;
	
	public TradeRecommendation(Message message, Double score) {
		super(message);
		createdAt = new Date();
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
		return "Trade: " + getMessage();
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
