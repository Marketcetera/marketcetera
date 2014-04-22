package org.marketcetera.core.instruments;

import org.marketcetera.trade.ConvertibleBond;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.CFICode;
import quickfix.field.SecurityType;

/* $License$ */

/**
 * Extracts a <code>ConvertibleBond</code> instrument from a FIX Message.
 * 
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ConvertibleBondFromMessage
        extends InstrumentFromMessage
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.InstrumentFromMessage#extract(quickfix.Message)
     */
    @Override
    public Instrument extract(Message inMessage)
    {
        String symbol = getSymbol(inMessage);
        return symbol == null ? null : new ConvertibleBond(symbol);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.DynamicInstrumentHandler#isHandled(java.lang.Object)
     */
    @Override
    protected boolean isHandled(Message inValue)
    {
        try {
            return (!(inValue.isSetField(CFICode.FIELD))) &&
                    ((!inValue.isSetField(SecurityType.FIELD)) ||
                      SecurityType.CONVERTIBLE_BOND.equals(inValue.getString(SecurityType.FIELD)));
        } catch (FieldNotFound ignore) {
            return false;
        }
    }
}
