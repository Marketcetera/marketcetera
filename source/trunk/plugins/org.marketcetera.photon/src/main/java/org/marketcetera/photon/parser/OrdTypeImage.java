package org.marketcetera.photon.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import quickfix.field.OrdType;


/**
 * OrdTypeImage contains the constants used to specify an OrdType in an order.
 *
 * This is mostly a synthetic image - we don't parse it, but we use the
 * images to shorten displays in how we display messages - this is a custom
 * behaviour compared to just showing regular "human-readable" values of FIX data dictionary
 *
 * @author toli
 *
 */
public enum OrdTypeImage implements ILexerFIXImage{
    LIM("LIM", OrdType.LIMIT),
    MKT("MKT", OrdType.MARKET),
    LOC("LOC", OrdType.LIMIT_ON_CLOSE),
    MOC("MOC", OrdType.MARKET_ON_CLOSE),
    FX_LIM("FX_LIM", OrdType.FOREX_LIMIT),
    FX_MKT("FX_MKT", OrdType.FOREX_MARKET);

	static final Map<String, OrdTypeImage> nameMap = new HashMap<String, OrdTypeImage>();
    static final Map<Character, OrdTypeImage> fixValueMap = new HashMap<Character, OrdTypeImage>();
	private static final String[] images;

	static {
		ArrayList<String> imageList = new ArrayList<String>();
		for (OrdTypeImage anImage : OrdTypeImage.values()) {
			nameMap.put(anImage.getImage(), anImage);
            fixValueMap.put(anImage.getFIXCharValue(), anImage);
			imageList.add(anImage.getImage());
		}
		images = imageList.toArray(new String[imageList.size()]);
	}

	private String image;
	private final char fixValue;


	OrdTypeImage(String anImage, char fixValue) {
        this.image = anImage;
		this.fixValue = fixValue;
    }

	public String getImage() {
		return image;
	}
	public static OrdTypeImage fromName(String image) {
		return nameMap.get(image);
	}

    public static OrdTypeImage fromFIXValue(char value) {
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
		return ""+fixValue;
	}

}
