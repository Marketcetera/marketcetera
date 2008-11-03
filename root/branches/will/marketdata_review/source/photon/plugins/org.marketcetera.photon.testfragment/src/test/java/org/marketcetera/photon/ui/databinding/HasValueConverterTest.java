package org.marketcetera.photon.ui.databinding;

import junit.framework.TestCase;

public class HasValueConverterTest extends TestCase {

	public void testConvert() throws Exception {
		HasValueConverter converter = new HasValueConverter();
		assertFalse((Boolean) converter.convert(null));
		assertFalse((Boolean) converter.convert(""));
		assertTrue((Boolean) converter.convert(9));
		assertTrue((Boolean) converter.convert("asdf"));
	}
}
