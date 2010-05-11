package org.marketcetera.core.instruments;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MaturityDate;
import quickfix.field.MaturityDay;
import quickfix.field.MaturityMonthYear;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class ExpirableInstrumentFromMessage
        extends InstrumentFromMessage
{
    /**
     * Returns the expiry value for the specified FIX message.
     *
     * @param inMessage the FIX message
     *
     * @return the expiry value, if available, null otherwise.
     */
    protected static String getExpiry(Message inMessage)
    {
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
