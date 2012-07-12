package org.marketcetera.photon.internal.marketdata;


import java.math.BigDecimal;

import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 * Test {@link Key}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class KeyTest extends KeyTestBase {

	private class KeySubclass extends Key {
		public KeySubclass(Instrument instrument) {
			super(instrument);
		}
	};

	@Override
	Object createKey1() {
		return new KeySubclass(new Equity("IBM"));
	}

	@Override
	Object createKey2() {
		return new KeySubclass(new Option("METC", "200901", BigDecimal.ONE, OptionType.Call));
	}

	@Override
	Object createKeyLike1ButDifferentClass() {
		return new Key(new Equity("IBM")) {
		};
	}

	@Override
	void createKeyWithNullSymbol() {
		new Key(null) {
		};
	}

}
