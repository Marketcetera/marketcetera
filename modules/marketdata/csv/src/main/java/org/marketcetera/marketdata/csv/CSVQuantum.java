package org.marketcetera.marketdata.csv;

import java.util.Arrays;

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
 * @since 2.1.0
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
     * @param inReplayRate a <code>double</code> value containing the replay rate at which to process the events
     * @return a <code>CSVQuantum</code> value
     */
    static CSVQuantum getQuantum(String[] inLine,
                                 MarketDataRequest inRequest,
                                 double inReplayRate)
    {
        return new CSVQuantum(inLine,
                              inRequest,
                              inReplayRate);
    }
    /**
     * Get the receivedTimestamp value.
     *
     * @return a <code>long</code> value
     */
    public long getReceivedTimestamp()
    {
        return receivedTimestamp;
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
     * Get the replayRate value.
     *
     * @return a <code>double</code> value
     */
    public double getReplayRate()
    {
        return replayRate;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return Arrays.toString(line);
    }
    /**
     * Create a new CSVQuantum instance.
     *
     * @param inLine a <code>String[]</code> value
     * @param inRequest a <code>MarketDataRequest</code> value
     * @param inReplayRate a <code>double</code> value
     */
    private CSVQuantum(String[] inLine,
                       MarketDataRequest inRequest,
                       double inReplayRate)
    {
        line = inLine;
        request = inRequest;
        replayRate = inReplayRate;
    }
    /**
     * indicates when this data was "received" from the provider
     */
    private final long receivedTimestamp = System.currentTimeMillis();
    /**
     * a single line from the file
     */
    private final String[] line;
    /**
     * the original market data request
     */
    private final MarketDataRequest request;
    /**
     * rate at which to replay the events
     */
    private final double replayRate;
}
