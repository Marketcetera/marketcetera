package org.marketcetera.core.instruments;

import java.util.regex.Pattern;

import org.marketcetera.core.Messages;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.field.FutSettDate;
import quickfix.field.FutSettDate2;
import quickfix.field.SecurityType;
import quickfix.field.Symbol;
/**
 * Extracts <code>Currency</code> from a <code>Message</code>.
 *
 * @author <a href="mailto:richard.obrien@qmscapital.com">Richard O'Brien</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public class CurrencyFromMessage
        extends InstrumentFromMessage
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.InstrumentFromMessage#extract(quickfix.FieldMap)
     */
    @Override
    public Instrument extract(FieldMap inMessage)
    {
        Currency instrument = null;
        try {
            String symbol = inMessage.getString(Symbol.FIELD);
            if(!symbolPattern.matcher(symbol).matches()) {
                throw new IllegalArgumentException();
            }
            String[] components = symbol.split("/");
            String baseCCY = components[0];
            String plCCY = components[1];
            String nearTenor = "SP";
            if(inMessage.isSetField(FutSettDate.FIELD)) {
                nearTenor = inMessage.getString(FutSettDate.FIELD);
            }
            if(inMessage.isSetField(FutSettDate2.FIELD)) {
                String farTenor = inMessage.getString(FutSettDate2.FIELD);
                instrument = new Currency(baseCCY,
                                          plCCY,
                                          nearTenor,
                                          farTenor);
            } else {
                instrument = new Currency(baseCCY,
                                          plCCY,
                                          nearTenor);
            }
            if(inMessage.isSetField(quickfix.field.Currency.FIELD)) {
                String tradedCCY = inMessage.getString(quickfix.field.Currency.FIELD);
                instrument.setTradedCCY(tradedCCY);
            }
        } catch(Exception e) {
            String message = Messages.ERROR_CURRENCY_FROM_MESSAGE.getText(e.toString());
            throw new IllegalArgumentException(message);
        }
        return instrument;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.DynamicInstrumentHandler#isHandled(java.lang.Object)
     */
    @Override
    protected boolean isHandled(FieldMap inValue)
    {
        try {
            return (inValue.isSetField(SecurityType.FIELD) &&
                    SecurityType.FOREIGN_EXCHANGE_CONTRACT.equals(inValue.getString(SecurityType.FIELD)));
                    
        } catch (FieldNotFound ignore) {
            return false;
        }
    }
    /**
     * represents a valid currency pattern
     */
    private static final Pattern symbolPattern = Pattern.compile("^[\\w]{1,}/[\\w]{1,}$");
}
