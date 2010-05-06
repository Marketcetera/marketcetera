package org.marketcetera.marketdata.csv;

import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents a single item of data extracted from a CSV market data file and the meta-data necessary
 * to interpret it.
 * 
 * <p>Objects of this type are passed to {@link CSVFeedEventTranslator#toEvent(Object, String)} implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since $Release$
 * @version $Id$
 */
@ClassVersion("$Id$")
public class CSVQuantum
{
    /**
     * Gets a <code>CSVQuantum</code> object with the given properties. 
     *
     * @param inLine a <code>String[]</code> value containing the distinct tokens from a single line in a CSV file
     * @param inRequest a <code>MarketDataRequest</code> value containing the original market data request
     * @return a <code>CSVQuantum</code> value
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
     * <p>This method retrieves the discrete elements of a single line of a market data file
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
     * a single line from the file
     */
    private final String[] line;
    /**
     * the original market data request
     */
    private final MarketDataRequest request;
}
