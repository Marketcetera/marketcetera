package org.marketcetera.quickfix;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;

/**
 * An unsupported QuickFix message was received. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
public class UnsupportedMessageException
        extends CoreException
{
    private static final long serialVersionUID = -5489749428430923539L;

    /**
     * Create a new UnsupportedMessageException instance.
     *
     * @param inMessage
     */
    public UnsupportedMessageException(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }

    /**
     * Create a new UnsupportedMessageException instance.
     *
     * @param inNested
     */
    public UnsupportedMessageException(Throwable inNested)
    {
        super(inNested);
    }

    /**
     * Create a new UnsupportedMessageException instance.
     *
     * @param inMsg
     * @param inNested
     */
    public UnsupportedMessageException(Throwable inNested,
                                       I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
}
