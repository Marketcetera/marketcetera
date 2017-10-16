package org.marketcetera.marketdata;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates an error related to market data operation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataException.java 17251 2016-09-08 23:18:29Z colin $
 * @since 2.4.0
 */
@ClassVersion("$Id: MarketDataException.java 17251 2016-09-08 23:18:29Z colin $")
public class MarketDataException
        extends CoreException
{
    /**
     * Create a new MarketDataException instance.
     */
    public MarketDataException()
    {
    }
    /**
     * Create a new MarketDataException instance.
     *
     * @param inNested a <code>Throwable</code> value
     */
    public MarketDataException(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new MarketDataException instance.
     *
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public MarketDataException(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new MarketDataException instance.
     *
     * @param inNested a <code>Throwable</code> value
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public MarketDataException(Throwable inNested,
                               I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
    private static final long serialVersionUID = 1L;
}
