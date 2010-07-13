package org.marketcetera.core.instruments;

import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.CFICode;
import quickfix.field.MaturityMonthYear;
import quickfix.field.SecurityType;

/* $License$ */

/**
 * Extracts <code>Future</code> from a <code>Message</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class FutureFromMessage
        extends InstrumentFromMessage
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.InstrumentFromMessage#extract(quickfix.Message)
     */
    @Override
    public Instrument extract(Message inMessage)
    {
        String symbol = getSymbol(inMessage);
        String expiry = null;
        try {
            MaturityMonthYear mmy = new MaturityMonthYear();
            inMessage.getField(mmy);
            expiry = mmy.getValue();
        } catch (FieldNotFound ignored) {}
        if(symbol == null ||
           expiry == null) {
            return null;
        }
        return new Future(symbol,
                          expiry);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.DynamicInstrumentHandler#isHandled(java.lang.Object)
     */
    @Override
    protected boolean isHandled(Message inValue)
    {
        try {
            return (inValue.isSetField(SecurityType.FIELD) &&
                    SecurityType.FUTURE.equals(inValue.getString(SecurityType.FIELD))) ||
                    (inValue.isSetField(CFICode.FIELD) &&
                            CFICodeUtils.isFuture(inValue.getString(CFICode.FIELD)));
        } catch (FieldNotFound ignore) {
            return false;
        }
    }
}