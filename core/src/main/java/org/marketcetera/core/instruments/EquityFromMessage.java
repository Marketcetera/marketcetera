package org.marketcetera.core.instruments;

import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.field.CFICode;
import quickfix.field.SecurityType;

/* $License$ */
/**
 * Extracts an equity instrument from a FIX Message.
 * <p>
 * Returns a null value if the symbol for the equity is not found in the message.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class EquityFromMessage
        extends InstrumentFromMessage
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.InstrumentFromMessage#extract(quickfix.FieldMap)
     */
    @Override
    public Instrument extract(FieldMap inMessage)
    {
        String symbol = getSymbol(inMessage);
        return symbol == null ? null : new Equity(symbol);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.DynamicInstrumentHandler#isHandled(java.lang.Object)
     */
    @Override
    protected boolean isHandled(FieldMap inValue)
    {
        try {
            return (!(inValue.isSetField(CFICode.FIELD))) &&
                    ((!inValue.isSetField(SecurityType.FIELD)) ||
                            SecurityType.COMMON_STOCK.equals(inValue.getString(SecurityType.FIELD)));
        } catch (FieldNotFound ignore) {
            return false;
        }
    }
}
