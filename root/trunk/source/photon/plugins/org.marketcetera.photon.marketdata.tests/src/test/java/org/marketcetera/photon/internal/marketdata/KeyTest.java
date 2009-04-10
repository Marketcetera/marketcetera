package org.marketcetera.photon.internal.marketdata;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.marketcetera.photon.model.marketdata.MDItem;

/* $License$ */

/**
 * Test {@link Key}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class KeyTest {

	/**
	 * Test equals and hashCode, especially to ensure that different subclasses are not equal.
	 */
	@Test
	public void testEqualsAndHashCode() {
		class KeySubclass extends Key<MDItem> {
			public KeySubclass(String symbol) {
				super(symbol);
			}
		};
		Key<MDItem> one = new Key<MDItem>("IBM") {
		};
		Key<MDItem> two = new KeySubclass("IBM");
		Key<MDItem> three = new KeySubclass("IBM");
		Key<MDItem> four = new KeySubclass("METC");
		assertConsistent(one);
		assertConsistent(two);
		assertConsistent(three);
		assertConsistent(four);
		assertSymmetricUnequal(one, two);
		assertSymmetricEqual(two, three);
		assertSymmetricUnequal(two, four);
	}
	
	private void assertConsistent(Object object) {
		assertThat(object, is(object));
		assertThat(object.hashCode(), is(object.hashCode()));
	}
	
	private void assertSymmetricEqual(Object one, Object two) {
		assertThat(one, is(two));
		assertThat(two, is(one));
	}
	
	private void assertSymmetricUnequal(Object one, Object two) {
		assertThat(one, not(two));
		assertThat(two, not(one));
	}

}
