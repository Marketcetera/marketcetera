package org.marketcetera.photon.internal.marketdata;


import org.marketcetera.photon.model.marketdata.MDItem;

/* $License$ */

/**
 * Test {@link Key}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class KeyTest extends KeyTestBase {

	private class KeySubclass extends Key<MDItem> {
		public KeySubclass(String symbol) {
			super(symbol);
		}
	};

	@Override
	Object createKey1() {
		return new KeySubclass("IBM");
	}

	@Override
	Object createKey2() {
		return new KeySubclass("METC");
	}

	@Override
	Object createKeyLike1ButDifferentClass() {
		return new Key<MDItem>("IBM") {
		};
	}

	@Override
	void createKeyWithNullSymbol() {
		new Key<MDItem>(null) {
		};
	}

}
