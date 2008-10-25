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
 * @version $Id: BogusMessage.java 9458 2008-08-01 05:27:12Z colin $
 * @since 0.5.0
 */
@ClassVersion("$Id: BogusMessage.java 9458 2008-08-01 05:27:12Z colin $") //$NON-NLS-1$
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
