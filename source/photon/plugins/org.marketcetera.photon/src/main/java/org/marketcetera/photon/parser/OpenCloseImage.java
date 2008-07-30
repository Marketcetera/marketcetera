package org.marketcetera.photon.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.marketcetera.photon.Messages;

import quickfix.field.OpenClose;

public enum OpenCloseImage
    implements ILexerFIXImage, Messages
{
    OPEN(OPEN_LABEL.getText(),
         OpenClose.OPEN),
    CLOSE(CLOSE_LABEL.getText(),
          OpenClose.CLOSE);
	static final Map<String, OpenCloseImage> nameMap = new HashMap<String, OpenCloseImage>();
	private static final String[] images;

	static {
		ArrayList<String> imageList = new ArrayList<String>();
		for (OpenCloseImage anImage : OpenCloseImage.values()) {
			nameMap.put(anImage.getImage(), anImage);
			imageList.add(anImage.getImage());
		}
		images = imageList.toArray(new String[0]);
	}

	private String image;
	private final char fixValue;
	OpenCloseImage(String s, char fixValue) {
		image = s;
		this.fixValue = fixValue;
	}
	public String getImage() {
		return image;
	}
	public static OpenCloseImage fromName(String image) {
		return nameMap.get(image);
	}
	public static String [] getImages(){
		return images;
	}
	public char getFIXCharValue() {
		return fixValue;
	}
	public int getFIXIntValue() {
		return (int) fixValue;
	}
	public String getFIXStringValue() {
		return ""+fixValue; //$NON-NLS-1$
	}
}
