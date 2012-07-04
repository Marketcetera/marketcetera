package org.marketcetera.core.instruments;

import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 * {@link UnderlyingSymbolSupport} that just returns the instrument's symbol.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: MockUnderlyingSymbolSupport.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
public class MockUnderlyingSymbolSupport implements UnderlyingSymbolSupport {

    @Override
    public String getUnderlying(Instrument instrument) {
        return instrument == null? null : instrument.getSymbol();
    }

}
