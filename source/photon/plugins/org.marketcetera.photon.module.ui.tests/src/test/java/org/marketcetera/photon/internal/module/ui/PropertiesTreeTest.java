package org.marketcetera.photon.internal.module.ui;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Before;
import org.junit.Test;

/* $License$ */

/**
 * Test {@link PropertiesTree}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class PropertiesTreeTest {

	private PropertiesTree mFixture;

	@Before
	public void setUp() {
		mFixture = new PropertiesTree();
		mFixture.put("mdata.activ.user", "will");
		mFixture.put("mdata.activ.password", "passw0rd");
		mFixture.put("mdata.opentick.url", "http://example.com");
		mFixture.put("strategy.mystrategy.name", "My Strategy");
		mFixture.put("strategy.mystrategy.:.ticker", "IBM");
		mFixture.put("strategy.mystrategy.GOOG.ticker", "GOOG");
	}

	@Test
	public void getChildKeys() {
		assertArrayEquals(new Object[] {"mdata", "strategy"}, mFixture.getChildKeys("").toArray());
		assertArrayEquals(new Object[] {"mdata.activ", "mdata.opentick"}, mFixture.getChildKeys("mdata").toArray());
	}

}
