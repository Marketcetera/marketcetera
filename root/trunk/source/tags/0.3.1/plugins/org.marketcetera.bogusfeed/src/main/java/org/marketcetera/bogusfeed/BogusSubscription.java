package org.marketcetera.bogusfeed;

import org.marketcetera.core.MemoizedHashCombinator;
import org.marketcetera.marketdata.ISubscription;

import quickfix.StringField;

public class BogusSubscription implements ISubscription {

	private final String reqID;
	private String symbol;

	public BogusSubscription(String reqID) {
		this.reqID = reqID;
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

}
