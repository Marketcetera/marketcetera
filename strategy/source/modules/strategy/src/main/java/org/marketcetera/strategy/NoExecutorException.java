package org.marketcetera.strategy;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since $Release$
 */
public class NoExecutorException
        extends CoreException
{
    private static final long serialVersionUID = -6063301263510154713L;
    /**
     * Create a new NoExecutorException instance.
     *
     * @param inNested
     */
    public NoExecutorException(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new NoExecutorException instance.
     *
     * @param inMessage
     */
    public NoExecutorException(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new NoExecutorException instance.
     *
     * @param inNested
     * @param inMessage
     */
    public NoExecutorException(Throwable inNested,
                               I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
}
