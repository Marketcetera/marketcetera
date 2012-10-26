package org.marketcetera.core.instruments;

import org.marketcetera.api.systemmodel.instruments.Instrument;
import org.marketcetera.core.trade.EquityImpl;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.CFICode;
import quickfix.field.SecurityType;

/* $License$ */
/**
 * Extracts an equity instrument from a FIX Message.
 * <p>
 * Returns a null value if the symbol for the equity is not found in the message.
 *
 * @version $Id: EquityFromMessage.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
public class EquityFromMessage extends InstrumentFromMessage {

    @Override
    public Instrument extract(Message inMessage) {
        String symbol = getSymbol(inMessage);
        return symbol == null ? null : new EquityImpl(symbol);
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
