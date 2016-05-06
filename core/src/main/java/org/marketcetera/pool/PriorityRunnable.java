package org.marketcetera.pool;

import org.apache.commons.lang3.builder.CompareToBuilder;

/* $License$ */

/**
 * Provides a prioritizable {@link Runnable} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class PriorityRunnable
        implements Runnable, Comparable<PriorityRunnable>
{
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(PriorityRunnable inO)
    {
        return new CompareToBuilder().append(getPriority(),inO.getPriority()).toComparison();
    }
    /**
     * Get the priority value.
     *
     * @return an <code>int</code> value
     */
    public int getPriority()
    {
        return 0;
    }
}
