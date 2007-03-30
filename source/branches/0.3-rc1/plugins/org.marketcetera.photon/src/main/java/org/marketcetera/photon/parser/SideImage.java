package org.marketcetera.photon.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import quickfix.field.Side;

public enum SideImage implements ILexerImage {
	BUY("B", Side.BUY), SELL("S", Side.SELL), SELL_SHORT("SS", Side.SELL_SHORT), SELL_SHORT_EXEMPT("SSE", Side.SELL_SHORT_EXEMPT);
	static final Map<String, SideImage> nameMap = new HashMap<String, SideImage>();
	private static final String[] images;

	static {
		ArrayList<String> imageList = new ArrayList<String>();
		for (SideImage anImage : SideImage.values()) {
			nameMap.put(anImage.getImage(), anImage);
			imageList.add(anImage.getImage());
		}
		images = imageList.toArray(new String[0]);
	}

	private String image;
	private final char fixValue;
	SideImage(String s, char fixValue) {
		image = s;
		this.fixValue = fixValue;
	}
	public String getImage() {
		return image;
	}
	public static SideImage fromName(String image) {
		return nameMap.get(image);
	}
	public static String [] getImages(){
		return images;
	}
	public char getFIXValue() {
		return fixValue;
	}

}

