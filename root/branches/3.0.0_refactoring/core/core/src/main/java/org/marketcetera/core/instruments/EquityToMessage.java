package org.marketcetera.core.instruments;

import org.marketcetera.core.trade.Instrument;
import org.marketcetera.core.trade.impl.EquityImpl;

import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.field.Symbol;

/* $License$ */
/**
 * Adds the appropriate fields for an equity instrument to a FIX Message.
 *
 * @version $Id: EquityToMessage.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
public class EquityToMessage extends InstrumentToMessage<EquityImpl> {
    /**
     * Creates an instance.
     */
    public EquityToMessage() {
        super(EquityImpl.class);
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
}
