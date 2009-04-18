package org.marketcetera.photon.internal.marketdata;



/* $License$ */

/**
 * Test {@link LatestTickKey}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: KeyTest.java 10492 2009-04-14 00:11:35Z klim $
 * @since 1.5.0
 */
public class LatestTickKeyTest extends KeyTestBase {

	@Override
	Object createKey1() {
		return new LatestTickKey("IBM");
	}

	@Override
	Object createKey2() {
		return new LatestTickKey("METC");
	}

	@Override
	Object createKeyLike1ButDifferentClass() {
		return new LatestTickKey("IBM") {
		};
	}

	@Override
	void createKeyWithNullSymbol() {
		new LatestTickKey(null);
	}

}
