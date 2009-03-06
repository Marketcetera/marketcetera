package org.marketcetera.marketdata.bogus;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.marketdata.DataRequest;

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
    private final DataRequest request;
    private List<String> symbols = new ArrayList<String>();    
    public BogusMessage(DataRequest inRequest)
    {
        request = inRequest;
    }    
    public DataRequest getAsDataRequest()
    {
        return request;
    }
    void addSymbol(String inSymbol)
    {
        symbols.add(inSymbol);
    }
    public List<String> getSymbols()
    {
        return symbols;
    }
}
