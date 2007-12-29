package org.marketcetera.photon.parser;

import quickfix.field.OrdStatus;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.marketcetera.core.ClassVersion;

/**
 * OrdStatusImage contains the constants used to specify an OrdStatus in an order.
 *
 * This is mostly a synthetic image - we don't parse it, but we use the
 * images to shorten displays in how we display messages - this is a custom
 * behaviour compared to just showing regular "human-readable" values of FIX data dictionary
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public enum OrdStatusImage implements ILexerFIXImage {
    PARTIAL("PARTIAL", OrdStatus.PARTIALLY_FILLED),
    PEND_CANC("PEND_CANCEL", OrdStatus.PENDING_CANCEL),
    PEND_REPL("PEND_REPL", OrdStatus.PENDING_REPLACE);

	static final Map<String, OrdStatusImage> nameMap = new HashMap<String, OrdStatusImage>();
    static final Map<Character, OrdStatusImage> fixValueMap = new HashMap<Character, OrdStatusImage>();
	private static final String[] images;

	static {
		ArrayList<String> imageList = new ArrayList<String>();
		for (OrdStatusImage anImage : OrdStatusImage.values()) {
			nameMap.put(anImage.getImage(), anImage);
            fixValueMap.put(anImage.getFIXCharValue(), anImage);
			imageList.add(anImage.getImage());
		}
		images = imageList.toArray(new String[imageList.size()]);
	}

	private String image;
	private final char fixValue;


	OrdStatusImage(String anImage, char fixValue) {
        this.image = anImage;
		this.fixValue = fixValue;
    }

	public String getImage() {
		return image;
	}
	public static OrdStatusImage fromName(String image) {
		return nameMap.get(image);
	}

    public static OrdStatusImage fromFIXValue(char value) {
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
