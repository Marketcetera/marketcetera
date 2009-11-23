package org.marketcetera.core.instruments;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.Option;
import quickfix.Message;
import quickfix.FieldNotFound;
import quickfix.field.*;

import java.math.BigDecimal;

/* $License$ */
/**
 * Extracts option instrument from a FIX Message.
 * <p>
 * Returns a null value, if all the expected attributes for an option are not
 * found in the message.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class OptionFromMessage extends InstrumentFromMessage {

    @Override
    public Instrument extract(Message inMessage) {
        String symbol = getSymbol(inMessage);
        OptionType type = getOptionType(inMessage);
        BigDecimal strike = getStrikePrice(inMessage);
        String expiry = getExpiry(inMessage);
        if(symbol == null || type == null || strike == null || expiry == null) {
            return null;
        }
        return new Option(symbol, expiry, strike, type);
    }

    @Override
    protected boolean isHandled(Message inValue) {
        try {
            return (inValue.isSetField(SecurityType.FIELD) &&
                    SecurityType.OPTION.equals(inValue.getString(SecurityType.FIELD))) ||
                    (inValue.isSetField(CFICode.FIELD) &&
                            CFICodeUtils.isOption(inValue.getString(CFICode.FIELD)));
        } catch (FieldNotFound ignore) {
            return false;
        }
    }

    /**
     * Returns the option type value from the specified FIX message.
     * <p>
     * This method looks into the <code>PutOrCall</code> and
     * <code>CFICode</code> fields to determine the option type value,
     * giving precedence to former.
     *
     * @param inMessage the FIX message.
     *
     * @return the option type value, if available, null otherwise.
     */
    private static OptionType getOptionType(Message inMessage) {
        //Check the value from PutOrCall field
        if (inMessage.isSetField(quickfix.field.PutOrCall.FIELD)) {
            try {
                return OptionType.getInstanceForFIXValue(
                        inMessage.getInt(
                                quickfix.field.PutOrCall.FIELD));
            } catch (FieldNotFound ignore) {
            }
        }
        //Otherwise check the CFI code field
        if(inMessage.isSetField(CFICode.FIELD)) {
            try {
                return CFICodeUtils.getOptionType(
                        inMessage.getString(CFICode.FIELD));
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }

    /**
     * Returns the strike price value for the specified FIX message.
     *
     * @param inMessage the FIX message.
     *
     * @return the strike price value, if available, null otherwise.
     */
    private static BigDecimal getStrikePrice(Message inMessage) {
        if (inMessage.isSetField(StrikePrice.FIELD)) {
            try {
                return inMessage.getDecimal(StrikePrice.FIELD);
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }

    /**
     * Returns the expiry value for the specified FIX message.
     *
     * @param inMessage the FIX message
     *
     * @return the expiry value, if available, null otherwise.
     */
    private static String getExpiry(Message inMessage) {
        //FIX versions 4.1, 4.2, 4.3 use MaturityMonthYear
        if (inMessage.isSetField(MaturityMonthYear.FIELD)) {
            try {
                String value = inMessage.getString(MaturityMonthYear.FIELD);
                if (value != null) {
                    //FIX version 4.2 uses MaturityDay to specify the option's
                    //expiry day once OSI goes into effect.
                    if (value.length() == 6 && inMessage.isSetField(MaturityDay.FIELD)) {
                        try {
                            String day = inMessage.getString(MaturityDay.FIELD);
                            if (day != null) {
                                return value + day;
                            }
                        } catch (FieldNotFound ignore) {
                        }
                    }
                    return value;
                }
            } catch (FieldNotFound ignore) {
            }
        }
        //FIX version 4.3 uses MaturityDate
        if (inMessage.isSetField(MaturityDate.FIELD)) {
            try {
                return inMessage.getString(MaturityDate.FIELD);
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }
}