package org.marketcetera.photon.parser;

import junit.framework.TestCase;

public class CancelReplaceTypeImageTest extends TestCase {

	public void testGetImage() {
		assertEquals("P", CancelReplaceTypeImage.PRICE.getImage());
		assertEquals("Q", CancelReplaceTypeImage.QUANTITY.getImage());
	}

	public void testFromName() {
		assertEquals(CancelReplaceTypeImage.PRICE, CancelReplaceTypeImage.fromName("P"));
		assertEquals(CancelReplaceTypeImage.QUANTITY, CancelReplaceTypeImage.fromName("Q"));
	}

	public void testGetImages() {
		String[] images = CancelReplaceTypeImage.getImages();
		assertEquals("Q",images[0]);
		assertEquals("P", images[1]);
	}

}
