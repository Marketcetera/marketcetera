package org.marketcetera.bogusfeed;

import org.marketcetera.marketdata.ISubscription;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;

public class BogusSubscription implements ISubscription {

	private final String reqID;
	private String symbol;
	private StringField correlationField;


	public BogusSubscription(String reqID, String subscribeMsgType) {
		this.reqID = reqID;
		correlationField = FIXMessageUtil.getCorrelationField(FIXVersion.FIX44, subscribeMsgType);
	}

	public String getReqID() {
		return reqID;
	}

	public boolean equals(Object arg0) {
		return reqID.equals(arg0);
	}

	public int hashCode() {
		return reqID.hashCode();
	}

	public String toString() {
		return reqID.toString();
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public boolean isResponse(Message possibleResponse) {
		if (correlationField != null){
			try {
				possibleResponse.getField(correlationField);
			} catch (FieldNotFound e) {
				return false;
			}
			return this.reqID.equals(correlationField.getValue());
		} else {
			return false;
		}
		
	}

}
