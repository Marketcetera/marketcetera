package org.marketcetera.core.instruments;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.Instrument;
import org.marketcetera.quickfix.FIXVersion;
import quickfix.Message;
import quickfix.DataDictionary;
import quickfix.field.SecurityType;
import quickfix.field.Symbol;

/* $License$ */
/**
 * A function handler that sets the fields corresponding to the
 * instrument onto the supplied FIX message.
 * <p>
 * Typical usage is:
 * <pre>
 * Instrument instrument = ...
 * quickfix.DataDictionary dataDictionary = ...
 * String msgType = ...
 * quickfix.Message message = ...
 * InstrumentToMessage itm = {@link InstrumentToMessage}.{@link #SELECTOR}.{@link StaticInstrumentFunctionSelector#forInstrument(Instrument) forInstrument}(instrument);
 * if(itm.{@link #isSupported(quickfix.DataDictionary, String) isSupported}(dataDictionary,msgType)) {
 *     itm.{@link #set(Instrument, DataDictionary, String, Message) set}(instrument, dataDictionary, msgType, message);
 * }
 * </pre>
 *
 * @param <I> The type of instrument handled by this function
 * 
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public abstract class InstrumentToMessage<I extends Instrument> extends InstrumentFunctionHandler<I> {

    /**
     * Sets the fields corresponding to the supplied instrument onto the
     * specified FIX message.
     * <p>
     * The choice of which fields are set in the message is dictated by
     * the FIX version value specified in the <code>inBeginString</code>.
     * <p>
     * <b>NOTE:</b> This method is only meant to be used for unit testing.
     * It's not recommended that this method be used in production. Use
     * {@link #set(org.marketcetera.trade.Instrument,quickfix.DataDictionary, String,quickfix.Message)} instead.
     *
     * @param inInstrument the instrument
     * @param inBeginString the begin string value of the FIX message
     * @param inMessage the FIX message
     */
    public abstract void set(Instrument inInstrument,
                             String inBeginString,
                             Message inMessage);

    /**
     * Returns true if the instrument represented by this function is supported
     * by the supplied data dictionary.
     *
     * @param inDictionary the data dictionary.
     * @param inMsgType the FIX message type
     * 
     * @return if the instrument is supported by the data dictionary.
     */
    public abstract boolean isSupported(DataDictionary inDictionary,
                                        String inMsgType);

    /**
     * Sets the fields corresponding to the supplied instrument onto the
     * specified FIX message as dictated by the data dictionary.
     *
     * @param inInstrument the instrument
     * @param inDictionary the data dictionary
     * @param inMsgType the FIX message type
     * @param inMessage the FIX message
     */
    public abstract void set(Instrument inInstrument,
                             DataDictionary inDictionary,
                             String inMsgType,
                             Message inMessage);
    
    /**
     * Creates an instance that handles the specified instrument subclass.
     *
     * @param inInstrument the instrument subclass handled by this instance.
     */
    protected InstrumentToMessage(Class<I> inInstrument) {
        super(inInstrument);
    }

    /**
     * Sets the security type field on the instrument if the FIX version is
     * greater than 4.0.
     *
     * @param inInstrument the instrument
     * @param inBeginString the begin string (fix version) value of the FIX message
     * @param inMessage the FIX message.
     */
    protected static void setSecurityType(Instrument inInstrument,
                                          String inBeginString,
                                          Message inMessage) {
        if((!FIXVersion.FIX40.equals(FIXVersion.getFIXVersion(inBeginString))) &&
                inInstrument.getSecurityType() != null &&
                org.marketcetera.trade.SecurityType.Unknown != inInstrument.getSecurityType()) {
            inMessage.setField(new SecurityType(inInstrument.getSecurityType().getFIXValue()));
        }
    }

    /**
     * Sets the security type on the message from the instrument if the FIX
     * dictionary supports the security type field and the specific value for
     * that field in the instrument.
     *
     * @param inInstrument the instrument.
     * @param inDictionary the FIX dictionary
     * @param inMsgType the FIX message type
     * @param inMessage the FIX message
     */
    protected static void setSecurityType(Instrument inInstrument,
                                          DataDictionary inDictionary,
                                          String inMsgType, Message inMessage) {
        if(inInstrument.getSecurityType() != null &&
                inDictionary.isMsgField(inMsgType,SecurityType.FIELD)) {
            String fixValue = inInstrument.getSecurityType().getFIXValue();
            if(inDictionary.isFieldValue(SecurityType.FIELD, fixValue)) {
                inMessage.setField(new SecurityType(fixValue));
            }
        }
    }

    /**
     * Sets the symbol field on the instrument if the FIX dictionary
     * supports the symbol field.
     *
     * @param inInstrument the instrument.
     * @param inDictionary the FIX dictionary
     * @param inMsgType the FIX message type
     * @param inMessage the FIX message
     */
    protected static void setSymbol(Instrument inInstrument,
                                    DataDictionary inDictionary,
                                    String inMsgType, Message inMessage) {
        if(inDictionary.isMsgField(inMsgType, Symbol.FIELD)) {
            inMessage.setField(new Symbol(inInstrument.getSymbol()));
        }
    }

    /**
     * The factory that provides the handler instance for the specified
     * instrument. 
     */
    @SuppressWarnings("rawtypes")
    public static final StaticInstrumentFunctionSelector<InstrumentToMessage> SELECTOR = new StaticInstrumentFunctionSelector<InstrumentToMessage>(InstrumentToMessage.class);
}
