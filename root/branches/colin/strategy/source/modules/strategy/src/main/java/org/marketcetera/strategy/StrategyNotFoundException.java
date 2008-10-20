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
public class StrategyNotFoundException
        extends CoreException
{
    private static final long serialVersionUID = 1780275202503116809L;
    /**
     * Create a new StrategyNotFoundException instance.
     *
     * @param inNested a <code>Throwable</code> value
     */
    public StrategyNotFoundException(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new StrategyNotFoundException instance.
     *
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public StrategyNotFoundException(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new StrategyNotFoundException instance.
     *
     * @param inNested a <code>Throwable</code> value
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public StrategyNotFoundException(Throwable inNested,
            I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
}
