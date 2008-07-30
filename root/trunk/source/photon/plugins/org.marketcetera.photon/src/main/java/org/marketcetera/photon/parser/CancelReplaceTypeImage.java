package org.marketcetera.photon.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * CancelReplaceTypeImage represents a discriminator for cancel replace orders.
 * Currently the command line interface of Photon only supports modifying either
 * the quantity or price of an order, not both simultaneously.
 * 
 * @author gmiller
 *
 */
enum CancelReplaceTypeImage {
    QUANTITY("Q"), //$NON-NLS-1$
    PRICE("P"); //$NON-NLS-1$

	static final Map<String, CancelReplaceTypeImage> nameMap = new HashMap<String, CancelReplaceTypeImage>();
	private static final String[] images;

	static {
		ArrayList<String> imageList = new ArrayList<String>();
		for (CancelReplaceTypeImage anImage : CancelReplaceTypeImage.values()) {
			nameMap.put(anImage.getImage(), anImage);
			imageList.add(anImage.getImage());
		}
		images = imageList.toArray(new String[0]);
	}

	private String image;

    
	CancelReplaceTypeImage(String anImage) {
        this.image = anImage;
    }
    
	public String getImage() {
		return image;
	}
	public static CancelReplaceTypeImage fromName(String image) {
		return nameMap.get(image);
	}
	public static String [] getImages(){
		return images;
	}


}
