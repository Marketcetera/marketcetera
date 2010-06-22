package org.marketcetera.core.instruments;

import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.Future;

import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.field.MaturityDate;
import quickfix.field.MaturityMonthYear;
import quickfix.field.Symbol;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FutureToMessage
        extends ExpirableInstrumentToMessage<Future>
{
    /**
     * Create a new FutureToMessage instance.
     */
    public FutureToMessage()
    {
        super(Future.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.InstrumentToMessage#isSupported(quickfix.DataDictionary, java.lang.String)
     */
    @Override
    public boolean isSupported(DataDictionary inDictionary,
                               String inMsgType)
    {
        // dictionary supports means to specify the 2 attributes of a future
        return (inDictionary.isMsgField(inMsgType,Symbol.FIELD)) &&
                (inDictionary.isMsgField(inMsgType,
                                         MaturityDate.FIELD) ||
                 inDictionary.isMsgField(inMsgType,
                                         MaturityMonthYear.FIELD));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.InstrumentToMessage#set(org.marketcetera.trade.Instrument, java.lang.String, quickfix.Message)
     */
    @Override
    public void set(Future inInstrument,
                    String inBeginString,
                    Message inMessage)
    {
        if(FIXVersion.FIX40.equals(FIXVersion.getFIXVersion(inBeginString))) {
            throw new IllegalArgumentException(
                    Messages.FUTURES_NOT_SUPPORTED_FOR_FIX_VERSION.getText(inBeginString));
        }
        inMessage.setField(new Symbol(inInstrument.getSymbol()));
        setSecurityTypeAndExpiry(inInstrument,
                                 inBeginString,
                                 inMessage);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.InstrumentToMessage#set(org.marketcetera.trade.Instrument, quickfix.DataDictionary, java.lang.String, quickfix.Message)
     */
    @Override
    public void set(Future inInstrument,
                    DataDictionary inDictionary,
                    String inMsgType,
                    Message inMessage)
    {
        setSecurityType(inInstrument,
                        inDictionary,
                        inMsgType,
                        inMessage);
        setSymbol(inInstrument,
                  inDictionary,
                  inMsgType,
                  inMessage);
        setExpiry(inInstrument,
                  inMsgType,
                  inDictionary,
                  inMessage);
    }
}
