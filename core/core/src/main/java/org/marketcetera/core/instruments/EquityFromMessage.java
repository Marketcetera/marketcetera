package org.marketcetera.core.instruments;

import org.marketcetera.core.attributes.ClassVersion;
import org.marketcetera.core.trade.Instrument;
import org.marketcetera.core.trade.Equity;
import quickfix.Message;
import quickfix.FieldNotFound;
import quickfix.field.SecurityType;
import quickfix.field.CFICode;

/* $License$ */
/**
 * Extracts an equity instrument from a FIX Message.
 * <p>
 * Returns a null value if the symbol for the equity is not found in the message.
 *
 * @author anshul@marketcetera.com
 * @version $Id: EquityFromMessage.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: EquityFromMessage.java 16063 2012-01-31 18:21:55Z colin $")
public class EquityFromMessage extends InstrumentFromMessage {

    @Override
    public Instrument extract(Message inMessage) {
        String symbol = getSymbol(inMessage);
        return symbol == null ? null : new Equity(symbol);
    }

    @Override
    protected boolean isHandled(Message inValue) {
        try {
            return (!(inValue.isSetField(CFICode.FIELD))) &&
                    ((!inValue.isSetField(SecurityType.FIELD)) ||
                            SecurityType.COMMON_STOCK.equals(inValue.getString(SecurityType.FIELD)));
        } catch (FieldNotFound ignore) {
            return false;
        }
    }
}
