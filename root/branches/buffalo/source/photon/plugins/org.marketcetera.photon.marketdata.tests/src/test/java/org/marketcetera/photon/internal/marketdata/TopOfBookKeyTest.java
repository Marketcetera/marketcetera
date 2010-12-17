package org.marketcetera.photon.internal.marketdata;

import java.math.BigDecimal;

import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;



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
		return new TopOfBookKey(new Equity("IBM"));
	}

	@Override
	Object createKey2() {
		return new TopOfBookKey(new Option("IBM", "200901", BigDecimal.ONE, OptionType.Call));
	}

	@Override
	Object createKeyLike1ButDifferentClass() {
		return new TopOfBookKey(new Equity("IBM")) {
		};
	}

	@Override
	void createKeyWithNullSymbol() {
		new TopOfBookKey(null);
	}

}
