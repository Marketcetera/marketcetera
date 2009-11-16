package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.trade.Equity;

/* $License$ */

/**
 * Test {@link SharedOptionLatestTickKey}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class SharedOptionLatestTickKeyTest extends KeyTestBase {

	@Override
	Object createKey1() {
		return new SharedOptionLatestTickKey(new Equity("IBM"));
	}

	@Override
	Object createKey2() {
		return new SharedOptionLatestTickKey(new Equity("METC"));
	}

	@Override
	Object createKeyLike1ButDifferentClass() {
		return new SharedOptionLatestTickKey(new Equity("IBM")) {
		};
	}

	@Override
	void createKeyWithNullSymbol() {
		new SharedOptionLatestTickKey(null);
	}

}
