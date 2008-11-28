package org.marketcetera.photon.internal.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.internal.strategy.Strategy.Destination;
import org.marketcetera.photon.internal.strategy.Strategy.State;
import org.marketcetera.photon.test.IsExpectedPropertyChangeEvent;

/* $License$ */

/**
 * Test {@link Strategy}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyTest {

	private Strategy fixture;
	private PropertyChangeListener listener;

	@Before
	public void setUp() {
		fixture = createTestStrategy();
		listener = mock(PropertyChangeListener.class);
		fixture.addPropertyChangeListener("state", listener);
		fixture.addPropertyChangeListener("displayName", listener);
	}

	@Test
	public void propertyChange() {
		final String displayName = "ABC";
		fixture.setDisplayName(displayName);
		verify(listener).propertyChange(
				argThat(new IsExpectedPropertyChangeEvent("displayName", null,
						displayName)));
		final String displayName2 = "ABC2";
		fixture.setDisplayName(displayName2);
		verify(listener).propertyChange(
				argThat(new IsExpectedPropertyChangeEvent("displayName",
						displayName, displayName2)));
		fixture.setState(State.RUNNING);
		verify(listener).propertyChange(
				argThat(new IsExpectedPropertyChangeEvent("state", null,
						State.RUNNING)));
		fixture.setState(State.STOPPED);
		verify(listener).propertyChange(
				argThat(new IsExpectedPropertyChangeEvent("state",
						State.RUNNING, State.STOPPED)));
	}
	
	@Test
	public void testParameters() {
		assertTrue(fixture.getParameters().isEmpty());
		Properties props = new Properties();
		props.put("ABC", "DEF");
		props.put("XYZ", "123");
		fixture.setParameters(props);
		for (Map.Entry<Object, Object> entry : props.entrySet()) {
			assertEquals(entry.getValue(), fixture.getParameters().get(entry.getKey()));
		}
		fixture.setParameters(new Properties());
		assertTrue(fixture.getParameters().isEmpty());
	}

	public static Strategy createTestStrategy() {
		return new Strategy(new ModuleURN("metc:strategy:system:test"),
				null, "Null Strategy", Destination.SINK, new Properties());
	}
}
