package org.marketcetera.photon;

import junit.framework.TestCase;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;

public class FIXFieldLocalizerTest extends TestCase {
	public void testGetLocalizedFIXValueName() throws Exception {
		assertEquals("LMT", FIXFieldLocalizer.getLocalizedFIXValueName(OrdType.class.getSimpleName(), "LIMIT"));

		assertEquals("NEW", FIXFieldLocalizer.getLocalizedFIXValueName(OrdStatus.class.getSimpleName(), "NEW"));
		assertEquals("PARTIAL", FIXFieldLocalizer.getLocalizedFIXValueName(OrdStatus.class.getSimpleName(), "PARTIALLY FILLED"));
	}
}
