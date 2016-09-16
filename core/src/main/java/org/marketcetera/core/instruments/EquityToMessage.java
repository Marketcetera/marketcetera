package org.marketcetera.core.instruments;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;

import quickfix.Group;
import quickfix.Message;
import quickfix.DataDictionary;
import quickfix.field.Symbol;

/* $License$ */
/**
 * Adds the appropriate fields for an equity instrument to a FIX Message.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class EquityToMessage
        extends InstrumentToMessage<Equity>
{
    /**
     * Creates an instance.
     */
    public EquityToMessage() {
        super(Equity.class);
    }

    @Override
    public void set(Instrument inInstrument, String inBeginString, Message inMessage) {
        setSecurityType(inInstrument, inBeginString, inMessage);
        inMessage.setField(new Symbol(inInstrument.getSymbol()));
    }

    @Override
    public boolean isSupported(DataDictionary inDictionary, String inMsgType) {
        return inDictionary.isMsgField(inMsgType,Symbol.FIELD);
    }

    @Override
    public void set(Instrument inInstrument, DataDictionary inDictionary,
                    String inMsgType, Message inMessage) {
        setSecurityType(inInstrument, inDictionary, inMsgType, inMessage);
        setSymbol(inInstrument, inDictionary, inMsgType, inMessage);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.InstrumentToMessage#set(org.marketcetera.trade.Instrument, quickfix.DataDictionary, java.lang.String, quickfix.Group)
     */
    @Override
    public void set(Instrument inInstrument,
                    DataDictionary inDictionary,
                    String inMsgType,
                    Group inGroup)
    {
        setSecurityType(inInstrument,
                        inDictionary,
                        inMsgType,
                        inGroup);
        setSymbol(inInstrument,
                  inDictionary,
                  inMsgType,
                  inGroup);
    }
}
