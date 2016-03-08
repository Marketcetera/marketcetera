package org.marketcetera.symbol;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.options.OptionUtils;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Resolves symbols according to patterns.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public class PatternSymbolResolver
        implements SymbolResolver
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.symbol.SymbolResolver#resolveSymbol(java.lang.String)
     */
    @Override
    public Instrument resolveSymbol(String inSymbol)
    {
        inSymbol = StringUtils.trimToNull(inSymbol);
        if(inSymbol == null) {
            throw new NullPointerException();
        }
        try {
            return Future.fromString(inSymbol);
        } catch (IllegalArgumentException ignored) {}
        try {
            return OptionUtils.getOsiOptionFromString(inSymbol);
        } catch (IllegalArgumentException ignored) {}
        if(inSymbol.contains("/")) { //$NON-NLS-1$
            return new Currency(inSymbol);
        }
        return new Equity(inSymbol);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.symbol.SymbolResolver#generateSymbol(org.marketcetera.trade.Instrument)
     */
    @Override
    public String generateSymbol(Instrument inInstrument)
    {
        if(inInstrument instanceof Future) {
            Future future = (Future)inInstrument;
            return future.getFullSymbol();
        } else if(inInstrument instanceof Option) {
            Option option = (Option)inInstrument;
            return OptionUtils.getOsiSymbolFromOption(option);
        } else if(inInstrument instanceof Currency) {
            Currency currency = (Currency)inInstrument;
            return currency.getLeftCCY() + "/" + currency.getRightCCY();
        } else {
            return inInstrument.getSymbol();
        }
    }
}
