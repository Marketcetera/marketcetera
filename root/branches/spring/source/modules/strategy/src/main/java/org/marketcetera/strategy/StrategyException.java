package org.marketcetera.strategy;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 * An exception thrown during execution of a {@link Strategy}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class StrategyException
        extends CoreException
{
    private static final long serialVersionUID = -2152924775246996522L;
    /**
     * Create a new StrategyException instance.
     *
     * @param inNested
     */
    public StrategyException(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new StrategyException instance.
     *
     * @param inMessage
     */
    public StrategyException(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new StrategyException instance.
     *
     * @param inNested
     * @param inMessage
     */
    public StrategyException(Throwable inNested,
                             I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
}
