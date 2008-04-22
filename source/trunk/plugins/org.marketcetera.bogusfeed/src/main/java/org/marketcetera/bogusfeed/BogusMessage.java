package org.marketcetera.bogusfeed;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.core.MSymbol;

import quickfix.Message;

/**
 * Message format for {@link BogusFeed}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
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
