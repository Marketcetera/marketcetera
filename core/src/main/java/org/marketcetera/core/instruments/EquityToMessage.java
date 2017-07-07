package org.marketcetera.core.instruments;

import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.DataDictionary;
import quickfix.FieldMap;
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
    public void set(Instrument inInstrument, String inBeginString, FieldMap inMessage) {
        setSecurityType(inInstrument, inBeginString, inMessage);
        inMessage.setField(new Symbol(inInstrument.getSymbol()));
        if(inInstrument instanceof Equity) {
            Equity equity = (Equity)inInstrument;
            if(equity.getSymbolSfx() != null) {
                inMessage.setField(new quickfix.field.SymbolSfx(equity.getSymbolSfx()));
            }
        }
    }

    @Override
    public boolean isSupported(DataDictionary inDictionary, String inMsgType) {
        return inDictionary.isMsgField(inMsgType,Symbol.FIELD);
    }

    @Override
    public void set(Instrument inInstrument, DataDictionary inDictionary,
                    String inMsgType, FieldMap inMessage) {
        setSecurityType(inInstrument, inDictionary, inMsgType, inMessage);
        setSymbol(inInstrument, inDictionary, inMsgType, inMessage);
        setSymbolSfx(inInstrument,
                     inDictionary,
                     inMsgType,
                     inMessage);
    }
    /**
     * Sets the symbol sfx field on the instrument if the FIX dictionary supports the symbol field and the instrument has a non-null value.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inDictionary a <code>DataDictionary</code> value
     * @param inMsgType a <code>String</code> value
     * @param inMessage a <code>FieldMap</code> value
     */
    protected static void setSymbolSfx(Instrument inInstrument,
                                       DataDictionary inDictionary,
                                       String inMsgType,
                                       FieldMap inMessage)
    {
        if(inDictionary.isMsgField(inMsgType,
                                   quickfix.field.SymbolSfx.FIELD)) {
            Equity equity = (Equity)inInstrument;
            if(equity.getSymbolSfx() != null) {
                inMessage.setField(new quickfix.field.SymbolSfx(equity.getSymbolSfx()));
            }
        }
    }
}
