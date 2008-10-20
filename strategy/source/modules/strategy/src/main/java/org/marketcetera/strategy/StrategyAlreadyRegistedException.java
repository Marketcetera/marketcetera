package org.marketcetera.strategy;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
public class StrategyAlreadyRegistedException
        extends CoreException
{
    private static final long serialVersionUID = -3501247401362022533L;
    /**
     * Create a new StrategyAlreadyRegistedException instance.
     *
     * @param inNested
     */
    public StrategyAlreadyRegistedException(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new StrategyAlreadyRegistedException instance.
     *
     * @param inMessage
     */
    public StrategyAlreadyRegistedException(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new StrategyAlreadyRegistedException instance.
     *
     * @param inNested
     * @param inMessage
     */
    public StrategyAlreadyRegistedException(Throwable inNested,
            I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
}
