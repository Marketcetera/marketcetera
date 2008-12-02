package org.marketcetera.strategy;

import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates that a strategy could not be compiled.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
class CompilationFailed
        extends StrategyException
{
    private static final long serialVersionUID = -8620960760410053024L;
    /**
     * Create a new CompilationFailed instance.
     *
     * @param inNested
     */
    public CompilationFailed(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new CompilationFailed instance.
     *
     * @param inMessage
     */
    public CompilationFailed(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new CompilationFailed instance.
     *
     * @param inNested
     * @param inMessage
     */
    public CompilationFailed(Throwable inNested,
            I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
}
