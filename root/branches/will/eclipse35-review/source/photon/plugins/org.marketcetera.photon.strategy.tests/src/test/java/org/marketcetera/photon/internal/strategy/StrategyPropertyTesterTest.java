package org.marketcetera.photon.internal.strategy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.photon.internal.strategy.AbstractStrategyConnection.State;

/* $License$ */

/**
 * Test {@link StrategyPropertyTester}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class StrategyPropertyTesterTest {

	private StrategyPropertyTester mFixture;

	@Before
	public void setUp() {
		mFixture = new StrategyPropertyTester();
	}
	
	@Test
	public void illegalProperty() throws Exception {
		final String badProperty = "abc";
		new ExpectedFailure<IllegalArgumentException>(badProperty, false) {
			@Override
			protected void run() throws Exception {
				mFixture.test(StrategyTest.createTestStrategy(), badProperty, null, "STOPPED");
			}
		};
	}
	
	@Test
	public void testProperty() {
		AbstractStrategyConnection strategy = StrategyTest.createTestStrategy();
		strategy.setState(State.STOPPED);
		assertTrue(mFixture.test(strategy, "state", null, "STOPPED"));
		assertFalse(mFixture.test(strategy, "state", null, "RUNNING"));
		assertFalse(mFixture.test(strategy, "state", null, "ABC"));
		strategy.setState(State.RUNNING);
		assertTrue(mFixture.test(strategy, "state", null, "RUNNING"));
		assertFalse(mFixture.test(strategy, "state", null, "STOPPED"));
		assertFalse(mFixture.test(strategy, "state", null, "ABC"));
	}

}
