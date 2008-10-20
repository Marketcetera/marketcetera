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
public class StrategyExecutionException
        extends CoreException
{
    private static final long serialVersionUID = -6989407719476927268L;
    /**
     * Create a new StrategyExecutionException instance.
     *
     * @param inNested
     */
    public StrategyExecutionException(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new StrategyExecutionException instance.
     *
     * @param inMessage
     */
    public StrategyExecutionException(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new StrategyExecutionException instance.
     *
     * @param inNested
     * @param inMessage
     */
    public StrategyExecutionException(Throwable inNested,
                                      I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
}
