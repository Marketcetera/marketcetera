package org.marketcetera.core.instruments;

import org.marketcetera.trade.Future;
import org.marketcetera.trade.FutureExpirationMonth;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
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
        FutureExpirationMonth expirationMonth = getExpirationMonth(inMessage);
        Integer expirationYear = getExpirationYear(inMessage);
        if(symbol == null ||
           expirationMonth == null ||
           expirationYear == null) {
            return null;
        }
        return new Future(symbol,
                          expirationMonth,
                          expirationYear);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.DynamicInstrumentHandler#isHandled(java.lang.Object)
     */
    @Override
    protected boolean isHandled(Message inValue)
    {
        try {
            return (inValue.isSetField(SecurityType.FIELD) &&
                    SecurityType.FUTURE.equals(inValue.getString(SecurityType.FIELD)));
        } catch (FieldNotFound ignore) {
            return false;
        }
    }
    /**
     * Returns the expiration month value from the given <code>Message</code>.
     *
     * @param inMessage a <code>Message</code> value
     *
     * @return a <code>FutureExpirationMonth</code> or <code>null</code>
     */
    private static FutureExpirationMonth getExpirationMonth(Message inMessage)
    {
        //FIX versions 4.1, 4.2, 4.3 use MaturityMonthYear
        if (inMessage.isSetField(MaturityMonthYear.FIELD)) {
            try {
                String value = inMessage.getString(MaturityMonthYear.FIELD);
                if (value != null) {
                    if(value.length() != 6) {
                        // this is an invalid MaturityMonthYear, pass
                        return null;
                    }
                    return FutureExpirationMonth.getByMonthOfYear(Integer.parseInt(value.substring(4)));
                }
            } catch (Exception ignore) {
            }
        }
        // TODO figure out if MaturityDate applies to Futures in 4.3
//        //FIX version 4.3 uses MaturityDate
//        if (inMessage.isSetField(MaturityDate.FIELD)) {
//            try {
//                return inMessage.getString(MaturityDate.FIELD);
//            } catch (FieldNotFound ignore) {
//            }
//        }
        return null;
    }
    /**
     * Returns the expiration year value from the given <code>Message</code>.
     *
     * @param inMessage a <code>Message</code> value
     *
     * @return an <code>Integer</code> or <code>null</code>
     */
    private static Integer getExpirationYear(Message inMessage)
    {
        //FIX versions 4.1, 4.2, 4.3 use MaturityMonthYear
        if (inMessage.isSetField(MaturityMonthYear.FIELD)) {
            try {
                String value = inMessage.getString(MaturityMonthYear.FIELD);
                if (value != null) {
                    if(value.length() != 6) {
                        // this is an invalid MaturityMonthYear, pass
                        return null;
                    }
                    return Integer.parseInt(value.substring(0,
                                                            4));
                }
            } catch (Exception ignore) {
            }
        }
        // TODO figure out if MaturityDate applies to Futures in 4.3
//        //FIX version 4.3 uses MaturityDate
//        if (inMessage.isSetField(MaturityDate.FIELD)) {
//            try {
//                return inMessage.getString(MaturityDate.FIELD);
//            } catch (FieldNotFound ignore) {
//            }
//        }
        return null;
    }
}