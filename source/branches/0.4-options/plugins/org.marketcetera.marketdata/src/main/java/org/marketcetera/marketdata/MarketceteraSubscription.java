package org.marketcetera.marketdata;

public class MarketceteraSubscription implements ISubscription, Comparable<String>, CharSequence {

	private final String value;
	private final String subscribeMsgType;
	
	public MarketceteraSubscription(String value, String subscribeMsgType) {
		this.value = value;
		this.subscribeMsgType = subscribeMsgType;
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
}
