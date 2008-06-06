package org.marketcetera.photon.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import quickfix.field.OrderCapacity;

public enum OrderCapacityImage implements ILexerFIXImage  {
	// todo: This mapping needs to be revised. See http://trac.marketcetera.org/trac.fcgi/ticket/185
    CUSTOMER("Customer", OrderCapacity.AGENCY), BROKERDEALER("Broker/Dealer", OrderCapacity.PRINCIPAL), MARKETMAKER("Market Maker", OrderCapacity.RISKLESS_PRINCIPAL);
//	CUSTOMER("Customer", CustomerOrFirm.CUSTOMER), BROKERDEALER("Broker/Dealer", CustomerOrFirm.FIRM), MARKETMAKER("Market Maker", CustomerOrFirm.FIRM);
	static final Map<String, OrderCapacityImage> nameMap = new HashMap<String, OrderCapacityImage>();
	private static final String[] images;

	static {
		ArrayList<String> imageList = new ArrayList<String>();
		for (OrderCapacityImage anImage : OrderCapacityImage.values()) {
			nameMap.put(anImage.getImage(), anImage);
			imageList.add(anImage.getImage());
		}
		images = imageList.toArray(new String[0]);
	}

	private String image;
	private final int fixValue;
	OrderCapacityImage(String s, int fixValue) {
		image = s;
		this.fixValue = fixValue;
	}
	public String getImage() {
		return image;
	}
	public static OrderCapacityImage fromName(String image) {
		return nameMap.get(image);
	}
	public static String [] getImages(){
		return images;
	}
	public char getFIXCharValue() {
		return (char) fixValue;
	}
	public int getFIXIntValue() {
		return fixValue;
	}
	public String getFIXStringValue() {
		return ""+fixValue;
	}
}
