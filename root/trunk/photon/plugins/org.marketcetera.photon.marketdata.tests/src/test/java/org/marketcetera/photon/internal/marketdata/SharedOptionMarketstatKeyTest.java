package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.trade.Equity;

/* $License$ */

/**
 * Test {@link SharedOptionMarketstatKey}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class SharedOptionMarketstatKeyTest extends KeyTestBase {

	@Override
	Object createKey1() {
		return new SharedOptionMarketstatKey(new Equity("IBM"));
	}

	@Override
	Object createKey2() {
		return new SharedOptionMarketstatKey(new Equity("METC"));
	}

	@Override
	Object createKeyLike1ButDifferentClass() {
		return new SharedOptionMarketstatKey(new Equity("IBM")) {
		};
	}

	@Override
	void createKeyWithNullSymbol() {
		new SharedOptionLatestTickKey(null);
	}

}
