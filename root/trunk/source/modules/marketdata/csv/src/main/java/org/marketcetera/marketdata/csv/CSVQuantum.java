package org.marketcetera.marketdata.csv;

import org.marketcetera.marketdata.MarketDataRequest;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class CSVQuantum
{
    /**
     * 
     *
     *
     * @param inLine
     * @param inRequest
     * @return
     */
    static CSVQuantum getQuantum(String[] inLine,
                                 MarketDataRequest inRequest)
    {
        return new CSVQuantum(inLine,
                              inRequest);
    }
    /**
     * Get the line value.
     *
     * @return a <code>String[]</code> value
     */
    public String[] getLine()
    {
        return line;
    }
    /**
     * Get the request value.
     *
     * @return a <code>MarketDataRequest</code> value
     */
    public MarketDataRequest getRequest()
    {
        return request;
    }
    /**
     * Create a new CSVQuantum instance.
     *
     * @param inLine
     * @param inRequest
     */
    private CSVQuantum(String[] inLine,
                       MarketDataRequest inRequest)
    {
        line = inLine;
        request = inRequest;
    }
    /**
     * 
     */
    private final String[] line;
    /**
     * 
     */
    private final MarketDataRequest request;
}
