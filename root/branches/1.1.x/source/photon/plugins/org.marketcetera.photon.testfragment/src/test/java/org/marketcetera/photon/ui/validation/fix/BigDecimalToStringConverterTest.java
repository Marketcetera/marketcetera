package org.marketcetera.photon.ui.validation.fix;

import java.math.BigDecimal;

import junit.framework.TestCase;

public class BigDecimalToStringConverterTest extends TestCase {
	public void testConvert() throws Exception {
		BigDecimalToStringConverter converter = new BigDecimalToStringConverter(false);
		String result = (String) converter.convert(new BigDecimal(2000));
		assertEquals("2000", result);
		
		assertEquals("2,000", new BigDecimalToStringConverter(true).convert(new BigDecimal(2000)));
	}
}
