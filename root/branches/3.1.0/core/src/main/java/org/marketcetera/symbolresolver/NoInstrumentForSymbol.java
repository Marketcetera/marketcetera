package org.marketcetera.symbolresolver;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates that no <code>Instrument</code> could be found for a given symbol.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: NoInstrumentForSymbol.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: NoInstrumentForSymbol.java 82384 2012-07-20 19:09:59Z colin $")
public class NoInstrumentForSymbol
        extends CoreException
{
    /**
     * Create a new NoInstrumentForSymbol instance.
     *
     * @param inNested a <code>Throwable</code> value
     */
    public NoInstrumentForSymbol(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new NoInstrumentForSymbol instance.
     *
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public NoInstrumentForSymbol(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new NoInstrumentForSymbol instance.
     *
     * @param inNested a <code>Throwable</code> value
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public NoInstrumentForSymbol(Throwable inNested,
                                 I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
    private static final long serialVersionUID = 1L;
}
