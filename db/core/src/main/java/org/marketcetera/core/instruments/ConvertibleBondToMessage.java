package org.marketcetera.core.instruments;

import java.util.EnumSet;
import java.util.Set;

import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.ConvertibleBond;
import org.marketcetera.trade.Instrument;

import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.field.Symbol;

/* $License$ */

/**
 * Prepares FIX messages with the fields necessary for <code>ConvertibleBond</code> instruments.
 *
 * @version $Id$
 * @since $Release$
 */
public class ConvertibleBondToMessage
        extends InstrumentToMessage<ConvertibleBond>
{
    /**
     * Create a new ConvertibleBondToMessage instance.
     */
    public ConvertibleBondToMessage()
    {
        super(ConvertibleBond.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.InstrumentToMessage#set(org.marketcetera.trade.Instrument, java.lang.String, quickfix.Message)
     */
    @Override
    public void set(Instrument inInstrument,
                    String inBeginString,
                    Message inMessage)
    {
        if(UNSUPPORTED_VERSIONS.contains(FIXVersion.getFIXVersion(inBeginString))) {
            throw new IllegalArgumentException(Messages.CONVERTIBLE_BONDS_NOT_SUPPORTED_FOR_FIX_VERSION.getText(inBeginString));
        }
        setSecurityType(inInstrument,
                        inBeginString,
                        inMessage);
        inMessage.setField(new Symbol(inInstrument.getSymbol()));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.InstrumentToMessage#isSupported(quickfix.DataDictionary, java.lang.String)
     */
    @Override
    public boolean isSupported(DataDictionary inDictionary,
                               String inMsgType)
    {
        return inDictionary.isMsgField(inMsgType,
                                       Symbol.FIELD);
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
    }
    /**
     * FIX versions that do not support convertible bonds
     */
    private static final Set<FIXVersion> UNSUPPORTED_VERSIONS = EnumSet.of(FIXVersion.FIX40, FIXVersion.FIX41);
}
