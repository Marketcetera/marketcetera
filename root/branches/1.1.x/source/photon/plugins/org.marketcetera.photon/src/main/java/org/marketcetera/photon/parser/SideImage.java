package org.marketcetera.photon.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import quickfix.field.Side;

public enum SideImage implements ILexerFIXImage {
	BUY("B", Side.BUY), SELL("S", Side.SELL), SELL_SHORT("SS", Side.SELL_SHORT); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	static final Map<String, SideImage> nameMap = new HashMap<String, SideImage>();
	static final Map<Character, SideImage> fixValueMap = new HashMap<Character, SideImage>();
	private static final String[] images;

	static {
		ArrayList<String> imageList = new ArrayList<String>();
		for (SideImage anImage : SideImage.values()) {
			nameMap.put(anImage.getImage(), anImage);
            fixValueMap.put(anImage.getFIXCharValue(), anImage);
            imageList.add(anImage.getImage());
		}
		images = imageList.toArray(new String[imageList.size()]);
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

	public static SideImage fromFIXValue(char value) {
		return fixValueMap.get(value);
	}

	public static String [] getImages(){
		return images;
	}
	
	public char getFIXCharValue() {
		return fixValue;
	}
	public int getFIXIntValue() {
		return fixValue;
	}
	
	public String getFIXStringValue() {
		return ""+fixValue; //$NON-NLS-1$
	}

}

