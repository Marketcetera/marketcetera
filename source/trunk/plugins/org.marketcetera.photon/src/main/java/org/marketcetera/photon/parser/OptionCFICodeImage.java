package org.marketcetera.photon.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.marketcetera.quickfix.cficode.OptionCFICode;

/**
 * FIX 4.3 +
 */
public enum OptionCFICodeImage implements ILexerFIXImage {

	PUT("P", "" + OptionCFICode.TYPE_PUT), CALL("C", ""+OptionCFICode.TYPE_CALL);
	static final Map<String, OptionCFICodeImage> nameMap = new HashMap<String, OptionCFICodeImage>();
	private static final String[] images;

	static {
		ArrayList<String> imageList = new ArrayList<String>();
		for (OptionCFICodeImage anImage : OptionCFICodeImage.values()) {
			nameMap.put(anImage.getImage(), anImage);
			imageList.add(anImage.getImage());
		}
		images = imageList.toArray(new String[0]);
	}

	private String image;
	private final String fixValue;
	OptionCFICodeImage(String s, String fixValue) {
		image = s;
		this.fixValue = fixValue;
	}
	public String getImage() {
		return image;
	}
	public static OptionCFICodeImage fromName(String image) {
		return nameMap.get(image);
	}
	public static String [] getImages(){
		return images;
	}
	public int getFIXIntValue() {
		return 0;
	}
	public char getFIXCharValue() {
		return 0;
	}
	public String getFIXStringValue() {
		return fixValue;
	}

}
