package org.marketcetera.bogusfeed;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MSymbol;

import quickfix.Message;

/* $License$ */

/**
 * Message format for {@link BogusFeed}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class BogusMessage
{
    private final Message mMessage;
    private List<MSymbol> mSymbols = new ArrayList<MSymbol>();    
    public BogusMessage(Message inMessage)
    {
        mMessage = inMessage;
    }    
    public Message getAsMessage()
    {
        return mMessage;
    }
    void addSymbol(MSymbol inSymbol)
    {
        mSymbols.add(inSymbol);
    }
    public List<MSymbol> getSymbols()
    {
        return mSymbols;
    }
}
