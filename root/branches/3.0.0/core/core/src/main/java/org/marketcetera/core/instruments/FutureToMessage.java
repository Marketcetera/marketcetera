package org.marketcetera.core.instruments;

import org.marketcetera.core.quickfix.FIXVersion;
import org.marketcetera.core.trade.Future;
import org.marketcetera.core.trade.Instrument;
import org.marketcetera.core.attributes.ClassVersion;

import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.field.CFICode;
import quickfix.field.MaturityDay;
import quickfix.field.MaturityMonthYear;
import quickfix.field.Symbol;

/* $License$ */

/**
 * Adds the appropriate fields for a future instrument to a FIX Message.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: FutureToMessage.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.1.0
 */
@ClassVersion("$Id: FutureToMessage.java 16063 2012-01-31 18:21:55Z colin $")
public class FutureToMessage
        extends InstrumentToMessage<Future>
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
                inDictionary.isMsgField(inMsgType,
                                        MaturityMonthYear.FIELD);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.InstrumentToMessage#set(org.marketcetera.trade.Instrument, java.lang.String, quickfix.Message)
     */
    @Override
    public void set(Instrument inInstrument,
                    String inBeginString,
                    Message inMessage)
    {
        if(FIXVersion.FIX40.equals(FIXVersion.getFIXVersion(inBeginString))) {
            throw new IllegalArgumentException(
                    Messages.FUTURES_NOT_SUPPORTED_FOR_FIX_VERSION.getText(inBeginString));
        }
        inMessage.setField(new Symbol(inInstrument.getSymbol()));
        Future future = (Future)inInstrument;
        int maturityDay = future.getExpirationDay();
        switch(FIXVersion.getFIXVersion(inBeginString)){
            case FIX_SYSTEM: //fall through
            case FIX41: //fall through
            case FIX42:
                setSecurityType(inInstrument,
                                inBeginString,
                                inMessage);
                inMessage.setField(future.getExpiryAsMaturityMonthYear());
                if(maturityDay != -1) {
                    inMessage.setField(new MaturityDay(String.valueOf(maturityDay)));
                }
                break;
            case FIX43:
                setCFICode(inMessage,
                           future);
                if(maturityDay != -1) {
                    inMessage.setField(new MaturityDay(String.valueOf(maturityDay)));
                }
                break;
            case FIX44:
                setCFICode(inMessage,
                           future);
                if(maturityDay != -1) {
                    inMessage.setField(new MaturityDay(String.valueOf(maturityDay)));
                }
                break;
            default:
                setCFICode(inMessage,
                           future);
                break;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.InstrumentToMessage#set(org.marketcetera.trade.Instrument, quickfix.DataDictionary, java.lang.String, quickfix.Message)
     */
    @Override
    public void set(Instrument inInstrument,
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
        Future future = (Future)inInstrument;
        //set as many fields as are available in the dictionary.
        if(inDictionary.isMsgField(inMsgType,
                                   CFICode.FIELD)) {
            setCFICode(inMessage,
                       future);
        }
        if(inDictionary.isMsgField(inMsgType, MaturityMonthYear.FIELD)) {
            inMessage.setField(future.getExpiryAsMaturityMonthYear());
        }
    }
    /**
     * Sets the CFI Code on the given <code>Message</code> for the given <code>Future</code>.
     *
     * @param inMessage a <code>Message</code> value
     * @param inFuture a <code>Future</code> value
     */
    private static void setCFICode(Message inMessage,
                                   Future inFuture)
    {
        String cfiCode = CFICodeUtils.getCFICode(inFuture);
        if(cfiCode != null) {
            inMessage.setField(new CFICode(cfiCode));
        }
        inMessage.setField(inFuture.getExpiryAsMaturityMonthYear());
    }
}
