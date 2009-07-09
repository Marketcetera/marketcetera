package org.marketcetera.core.position.impl;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

/* $License$ */

/**
 * Utility for asserting expected GlazedList list events.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class ExpectedListChanges<E> implements ListEventListener<E> {

	int current = 0;
	private final int[] expected;
	private final String name;

	public ExpectedListChanges(String name, int[] expected) {
		this.name = name;
		this.expected = expected;
	}

	@Override
	public void listChanged(ListEvent<E> listChanges) {
		while (listChanges.next()) {
			assertThat(name + " had an unexpected event", expected.length, greaterThan(current + 1));
			int change = current / 2;
			assertEquals(name + ", change " + change, expected[current], listChanges.getType());
			current++;
			assertEquals(name + ", change " + change, expected[current], listChanges.getIndex());
			current++;
		}
	}

	public void exhausted() {
		assertThat(name + " did not have enough events", current,
				greaterThanOrEqualTo(expected.length));
	}

}