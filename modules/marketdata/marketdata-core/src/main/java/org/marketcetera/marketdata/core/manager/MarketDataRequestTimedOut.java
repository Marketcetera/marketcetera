package org.marketcetera.marketdata.core.manager;

import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates that a market data request could not be executed in a reasonable amount of time.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataRequestTimedOut.java 16375 2012-11-19 21:02:22Z colin $
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public class MarketDataRequestTimedOut
        extends MarketDataRequestFailed
{
    /**
     * Create a new MarketDataRequestTimedOut instance.
     */
    public MarketDataRequestTimedOut()
    {
    }
    /**
     * Create a new MarketDataRequestTimedOut instance.
     *
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public MarketDataRequestTimedOut(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new MarketDataRequestTimedOut instance.
     *
     * @param inNested a <code>Throwable</code> value
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public MarketDataRequestTimedOut(Throwable inNested,
                                     I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
    /**
     * Create a new MarketDataRequestTimedOut instance.
     *
     * @param inNested a <code>Throwable</code> value
     */
    public MarketDataRequestTimedOut(Throwable inNested)
    {
        super(inNested);
    }
    private static final long serialVersionUID = 1L;
}
