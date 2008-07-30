package org.marketcetera.photon.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import quickfix.field.TimeInForce;


/**
 * TimeInForceImage contains the constants used to specify a time-in-force
 * limitation on an order.
 * 
 * @author gmiller
 *
 */
public enum TimeInForceImage implements ILexerFIXImage{
    DAY("DAY", TimeInForce.DAY),  //$NON-NLS-1$
    GTC("GTC", TimeInForce.GOOD_TILL_CANCEL),  //$NON-NLS-1$
    FOK("FOK", TimeInForce.FILL_OR_KILL),  //$NON-NLS-1$
    CLO("CLO", TimeInForce.AT_THE_CLOSE),  //$NON-NLS-1$
    OPG("OPG", TimeInForce.AT_THE_OPENING),  //$NON-NLS-1$
    IOC("IOC", TimeInForce.IMMEDIATE_OR_CANCEL);  //$NON-NLS-1$

	static final Map<String, TimeInForceImage> nameMap = new HashMap<String, TimeInForceImage>();
    static final Map<Character, TimeInForceImage> fixValueMap = new HashMap<Character, TimeInForceImage>();
	private static final String[] images;

	static {
		ArrayList<String> imageList = new ArrayList<String>();
		for (TimeInForceImage anImage : TimeInForceImage.values()) {
			nameMap.put(anImage.getImage(), anImage);
            fixValueMap.put(anImage.getFIXCharValue(), anImage);
			imageList.add(anImage.getImage());
		}
		images = imageList.toArray(new String[imageList.size()]);
	}

	private String image;
	private final char fixValue;

    
	TimeInForceImage(String anImage, char fixValue) {
        this.image = anImage;
		this.fixValue = fixValue;
    }
    
	public String getImage() {
		return image;
	}
	public static TimeInForceImage fromName(String image) {
		return nameMap.get(image);
	}

    public static TimeInForceImage fromFIXValue(char value) {
        return fixValueMap.get(value);
    }

	public static String [] getImages(){
		return images;
	}

	// todo: remove this method
	public char getFIXValue() {
		return getFIXCharValue();
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
