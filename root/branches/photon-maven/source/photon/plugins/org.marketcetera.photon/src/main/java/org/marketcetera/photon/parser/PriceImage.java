package org.marketcetera.photon.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * PriceImage contains only one constant "MKT", representing a market order
 * as other prices are represented as floating point numbers.
 * 
 * @author gmiller
 *
 */
public enum PriceImage implements ILexerImage {
    MKT("MKT"), LIMIT("0"); //$NON-NLS-1$ //$NON-NLS-2$
	static final Map<String, PriceImage> nameMap = new HashMap<String, PriceImage>();
	private static final String[] images;

	static {
		ArrayList<String> imageList = new ArrayList<String>();
		for (PriceImage anImage : PriceImage.values()) {
			nameMap.put(anImage.getImage(), anImage);
			imageList.add(anImage.getImage());
		}
		images = imageList.toArray(new String[0]);
	}
	private String image;

    PriceImage(String anImage) {
        this.image = anImage;
    }
	public String getImage() {
		return image;
	}
	public void setImage(String image){
		this.image = image;
	}
	public static PriceImage fromName(String image) {
		return nameMap.get(image);
	}
	public static String [] getImages(){
		return images;
	}

}