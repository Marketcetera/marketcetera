package org.marketcetera.photon.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * CommandImage contains the constant token values for the command string.
 * For example in the command to place an order to buy 100 shares of IBM for
 * 22.05, "O B 100 IBM 22.05", the command string is the first "O", and
 * stands for "order". The command string is always the first string in the
 * token stream.
 * 
 * @author gmiller
 * 
 */
public enum CommandImage implements ILexerImage {
	ORDER("O"), //$NON-NLS-1$
	CANCEL_ALL("CA"), //$NON-NLS-1$
    CANCEL("C"), //$NON-NLS-1$
    CANCEL_REPLACE("CXR"), //$NON-NLS-1$
    SET("SET"), //$NON-NLS-1$
    UNSET("UNSET"), //$NON-NLS-1$
	RESEND_REQUEST("RR"); //$NON-NLS-1$
	private static final Map<String, CommandImage> nameMap = new HashMap<String, CommandImage>();
	private static final String[] images;

	static {
		ArrayList<String> imageList = new ArrayList<String>();
		for (CommandImage anImage : CommandImage.values()) {
			nameMap.put(anImage.getImage(), anImage);
			imageList.add(anImage.getImage());
		}
		images = imageList.toArray(new String[0]);
	}
	private String image;

    CommandImage(String anImage) {
        this.image = anImage;
    }
	public String getImage() {
		return image;
	}
	public static CommandImage fromName(String image) {
		return nameMap.get(image);
	}
	public static String [] getImages(){
		return images;
	}
}
