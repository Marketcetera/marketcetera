package org.marketcetera.core.symbolresolver;

import org.marketcetera.core.CoreException;
import org.marketcetera.core.util.log.I18NBoundMessage;
import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */

/**
 * Indicates that no <code>Instrument</code> could be found for a given symbol.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: NoInstrumentForSymbol.java 82347 2012-05-03 19:30:54Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: NoInstrumentForSymbol.java 82347 2012-05-03 19:30:54Z colin $")
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
