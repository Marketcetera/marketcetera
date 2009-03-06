package org.marketcetera.photon.internal.strategy;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.beans.PropertyChangeListener;

import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.photon.internal.strategy.AbstractStrategyConnection.State;
import org.marketcetera.photon.test.IsExpectedPropertyChangeEvent;

/* $License$ */

/**
 * Tests {@link AbstractStrategyConnection}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class AbstractStrategyConnectionTest {

	/**
	 * Test constructor.
	 */
	@Test
	public void construction() {
		AbstractStrategyConnection fixture = new AbstractStrategyConnection("Test") {};
		assertThat(fixture.getDisplayName(), is("Test"));
		assertThat(fixture.getState(), is(State.STOPPED));
	}
	
	/**
	 * Test that null arguments fail.
	 */
	@Test
	public void nulls() throws Exception {
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new AbstractStrategyConnection(null) {};
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new AbstractStrategyConnection("Test") {}.setState(null);
			}
		};
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				new AbstractStrategyConnection("Test") {}.setDisplayName(null);
			}
		};
	}
	
	/**
	 * Test that property change events fire.
	 */
	@Test
	public void propertyChange() {
		AbstractStrategyConnection fixture = new AbstractStrategyConnection("Test") {};
		PropertyChangeListener listener = mock(PropertyChangeListener.class);
		fixture.addPropertyChangeListener("state", listener);
		fixture.addPropertyChangeListener("displayName", listener);
		final String displayName = "ABC";
		fixture.setDisplayName(displayName);
		verify(listener).propertyChange(
				argThat(new IsExpectedPropertyChangeEvent("displayName", "Test",
						displayName)));
		final String displayName2 = "ABC2";
		fixture.setDisplayName(displayName2);
		verify(listener).propertyChange(
				argThat(new IsExpectedPropertyChangeEvent("displayName",
						displayName, displayName2)));
		fixture.setState(State.RUNNING);
		verify(listener).propertyChange(
				argThat(new IsExpectedPropertyChangeEvent("state", State.STOPPED,
						State.RUNNING)));
		fixture.setState(State.STOPPED);
		verify(listener).propertyChange(
				argThat(new IsExpectedPropertyChangeEvent("state",
						State.RUNNING, State.STOPPED)));
	}

}