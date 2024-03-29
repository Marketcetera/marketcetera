package org.marketcetera.core.instruments;

import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.field.Symbol;

/* $License$ */
/**
 * A function handler that extracts the instrument value
 * from a FIX message.
 * <p>
 * Typical usage is
 * <pre>Instrument instrument = InstrumentFromMessage.SELECTOR.forValue(quickfix.Message).extract(quickfix.Message);</pre>
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public abstract class InstrumentFromMessage
        extends DynamicInstrumentHandler<FieldMap>
{

    /**
     * Extracts the instrument from the supplied message.
     * The return value of this method is defined only if {@link #isHandled(Object)}
     * method returns true.
     *
     * @param inMessage the message from which the instrument has to be extracted.
     *
     * @return the instrument value, if available, null otherwise.
     */
    public abstract Instrument extract(FieldMap inMessage);
    /**
     * Fetches the symbol field value from the supplied FIX message.
     *
     * @param inMessage the FIX message.
     *
     * @return the symbol field value.
     */
    protected static String getSymbol(FieldMap inMessage) {
        if(inMessage.isSetField(Symbol.FIELD)) {
            try {
                return inMessage.getString(Symbol.FIELD);
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }
    /**
     * The selector that can be used to obtain the appropriate instance of
     * this class given an instrument instance. 
     */
    public static final DynamicInstrumentFunctionSelector<FieldMap,InstrumentFromMessage> SELECTOR = new DynamicInstrumentFunctionSelector<FieldMap,InstrumentFromMessage>(InstrumentFromMessage.class);
}
