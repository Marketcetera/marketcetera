package org.marketcetera.photon.internal.marketdata;



/* $License$ */

/**
 * Test {@link TopOfBookKey}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class TopOfBookKeyTest extends KeyTestBase {

	@Override
	Object createKey1() {
		return new TopOfBookKey("IBM");
	}

	@Override
	Object createKey2() {
		return new TopOfBookKey("METC");
	}

	@Override
	Object createKeyLike1ButDifferentClass() {
		return new TopOfBookKey("IBM") {
		};
	}

	@Override
	void createKeyWithNullSymbol() {
		new TopOfBookKey(null);
	}

}
