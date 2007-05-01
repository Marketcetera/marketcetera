package org.marketcetera.photon.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import quickfix.field.PutOrCall;

public enum PutOrCallImage implements ILexerFIXImage {

	PUT("P", PutOrCall.PUT), CALL("C", PutOrCall.CALL);
	static final Map<String, PutOrCallImage> nameMap = new HashMap<String, PutOrCallImage>();
	private static final String[] images;

	static {
		ArrayList<String> imageList = new ArrayList<String>();
		for (PutOrCallImage anImage : PutOrCallImage.values()) {
			nameMap.put(anImage.getImage(), anImage);
			imageList.add(anImage.getImage());
		}
		images = imageList.toArray(new String[0]);
	}

	private String image;
	private final int fixValue;
	PutOrCallImage(String s, int fixValue) {
		image = s;
		this.fixValue = fixValue;
	}
	public String getImage() {
		return image;
	}
	public static PutOrCallImage fromName(String image) {
		return nameMap.get(image);
	}
	public static String [] getImages(){
		return images;
	}
	public int getFIXIntValue() {
		return fixValue;
	}
	public char getFIXCharValue() {
		return (char) fixValue;
	}
}
