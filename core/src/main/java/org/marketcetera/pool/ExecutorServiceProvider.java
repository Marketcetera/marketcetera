package org.marketcetera.pool;

/* $License$ */

/**
 * Provides a common execution service.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ExecutorServiceProvider
{
    /**
     * Execute the given job according to its priority.
     *
     * @param inJob a <code>PriorityRunnable</code> value
     */
    void execute(PriorityRunnable inJob);
}
