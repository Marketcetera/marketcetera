package org.marketcetera.marketdata;

import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;

public class FIXCorrelationFieldSubscription implements ISubscription, Comparable<String>, CharSequence {

	private final String value;
	private final String subscribeMsgType;
	private Integer marketDepth;
	private StringField correlationField;
	
	public FIXCorrelationFieldSubscription(String value, String subscribeMsgType, Integer marketDepth) {
		this.value = value;
		this.subscribeMsgType = subscribeMsgType;
		this.marketDepth = marketDepth;
		correlationField = FIXMessageUtil.getCorrelationField(FIXVersion.FIX44, subscribeMsgType);
	}

	public int compareTo(String other) {
		return value.compareTo(other);
	}

	public char charAt(int index) {
		return value.charAt(index);
	}

	public int length() {
		return value.length();
	}

	public CharSequence subSequence(int start, int end) {
		return value.subSequence(start, end);
	}

	public String toString(){
		return value;
	}

	public String getSubscribeMsgType() {
		return subscribeMsgType;
	}

	public Integer getMarketDepth() {
		return marketDepth;
	}
	
	public boolean isResponse(Message possibleResponse) {
		if (correlationField != null){
			try {
				possibleResponse.getField(correlationField);
			} catch (FieldNotFound e) {
				return false;
			}
			return this.value.equals(correlationField.getValue());
		} else {
			return false;
		}
		
	}

	public String getCorrelationFieldValue() {
		return value;
	}
}
