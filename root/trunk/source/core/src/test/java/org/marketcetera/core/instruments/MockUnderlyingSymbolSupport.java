package org.marketcetera.core.instruments;

import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 * {@link UnderlyingSymbolSupport} that just returns the instrument's symbol.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class MockUnderlyingSymbolSupport implements UnderlyingSymbolSupport {

    @Override
    public String getUnderlying(Instrument instrument) {
        return instrument == null? null : instrument.getSymbol();
    }

}
