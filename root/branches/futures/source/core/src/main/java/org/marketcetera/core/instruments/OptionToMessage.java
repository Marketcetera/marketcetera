package org.marketcetera.core.instruments;

import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.field.*;

/* $License$ */
/**
 * Adds the appropriate fields for an option instrument to a FIX Message.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class OptionToMessage
        extends ExpirableInstrumentToMessage<Option>
{
    /**
     * Creates an instance that handles options.
     */
    public OptionToMessage() {
        super(Option.class);
    }

    @Override
    public void set(Option inInstrument, String inBeginString, Message inMessage) {
        if(FIXVersion.FIX40.equals(FIXVersion.getFIXVersion(inBeginString))) {
            throw new IllegalArgumentException(
                    Messages.OPTION_NOT_SUPPORTED_FOR_FIX_VERSION.getText(inBeginString));
        }
        inMessage.setField(new Symbol(inInstrument.getSymbol()));
        inMessage.setField(new StrikePrice(inInstrument.getStrikePrice()));
        setSecurityTypeAndExpiry(inInstrument,
                                 inBeginString,
                                 inMessage);
        switch(FIXVersion.getFIXVersion(inBeginString)) {
            case FIX_SYSTEM: //fall through
            case FIX41: //fall through
            case FIX42:
               inMessage.setField(new PutOrCall(inInstrument.getType().getFIXValue()));
                break;
            // do nothing on default
        }
    }
    @Override
    public boolean isSupported(DataDictionary inDictionary, String inMsgType) {
        //if dictionary supports means to specify the 5 attributes of an option
        return
                //symbol
                (inDictionary.isMsgField(inMsgType,Symbol.FIELD)) &&
                //strike
                (inDictionary.isMsgField(inMsgType,StrikePrice.FIELD)) &&
                //security and option type
                ((inDictionary.isMsgField(inMsgType,SecurityType.FIELD) &&
                        inDictionary.isMsgField(inMsgType,PutOrCall.FIELD)) ||
                        inDictionary.isMsgField(inMsgType,CFICode.FIELD)) &&
                //expiry
                (inDictionary.isMsgField(inMsgType,MaturityDate.FIELD) ||
                        inDictionary.isMsgField(inMsgType,MaturityMonthYear.FIELD));
    }

    @Override
    public void set(Option inInstrument, DataDictionary inDictionary,
                    String inMsgType, Message inMessage) {
        setSecurityType(inInstrument, inDictionary, inMsgType, inMessage);
        setSymbol(inInstrument, inDictionary, inMsgType, inMessage);
        //set as many fields as are available in the dictionary.
        if(inDictionary.isMsgField(inMsgType, CFICode.FIELD)) {
            setCFICode(inMessage, inInstrument);
        }
        if(inDictionary.isMsgField(inMsgType, PutOrCall.FIELD)) {
            inMessage.setField(new PutOrCall(inInstrument.getType().getFIXValue()));
        }
        if(inDictionary.isMsgField(inMsgType, StrikePrice.FIELD)) {
            inMessage.setField(new StrikePrice(inInstrument.getStrikePrice()));
        }
        setExpiry(inInstrument,
                  inMsgType,
                  inDictionary,
                  inMessage);
    }
 }