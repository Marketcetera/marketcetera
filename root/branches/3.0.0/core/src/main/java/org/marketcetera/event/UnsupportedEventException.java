package org.marketcetera.event;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/**
 * An event was received from a data feed but could not be translated.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: UnsupportedEventException.java 16063 2012-01-31 18:21:55Z colin $
 */
@ClassVersion("$Id: UnsupportedEventException.java 16063 2012-01-31 18:21:55Z colin $")
public class UnsupportedEventException
        extends CoreException
{

    private static final long serialVersionUID = -5589876622054757393L;

    /**
     * Create a new UnsupportedEventException instance.
     *
     * @param inMessage
     */
    public UnsupportedEventException(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }

    /**
     * Create a new UnsupportedEventException instance.
     *
     * @param inNested
     */
    public UnsupportedEventException(Throwable inNested)
    {
        super(inNested);
    }

    /**
     * Create a new UnsupportedEventException instance.
     *
     * @param inNested
     * @param inMessage
     */
    public UnsupportedEventException(Throwable inNested,
                                     I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
}
