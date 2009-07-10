package org.marketcetera.photon.internal.marketdata;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;

public abstract class KeyTestBase {
	
	/**
	 * Test equals and hashCode, especially to ensure that different subclasses are not equal.
	 */
	@Test
	public void testEqualsAndHashCode() {
		Object oneA = createKey1();
		Object oneB = createKey1();
		Object two = createKey2();
		Object dif = createKeyLike1ButDifferentClass();
		assertConsistent(oneA);
		assertConsistent(oneB);
		assertConsistent(two);
		assertConsistent(dif);
		assertSymmetricEqual(oneA, oneB);
		assertSymmetricUnequal(oneA, two);
		assertSymmetricUnequal(oneB, two);
		assertSymmetricUnequal(oneA, dif);
		assertSymmetricUnequal(oneB, dif);
	}
	
	/**
	 * Test null symbol fails
	 */
	@Test
	public void testNullSymbolFails() throws Exception {
		new ExpectedFailure<IllegalArgumentException>(null) {
			@Override
			protected void run() throws Exception {
				createKeyWithNullSymbol();
			}
		};
	}
	
	abstract Object createKeyLike1ButDifferentClass();
	
	abstract Object createKey1();
	
	abstract Object createKey2();
	
	abstract void createKeyWithNullSymbol();
	
	public static void assertConsistent(Object object) {
		assertThat(object, is(object));
		assertThat(object.hashCode(), is(object.hashCode()));
	}

	public static void assertSymmetricEqual(Object one, Object two) {
		assertThat(one, is(two));
		assertThat(two, is(one));
	}

	public static void assertSymmetricUnequal(Object one, Object two) {
		assertThat(one, not(two));
		assertThat(two, not(one));
	}

}