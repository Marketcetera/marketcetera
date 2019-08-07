package org.marketcetera.cluster;

/* $License$ */

/**
 * Describes a cluster queue.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.5.0
 */
public interface QueueDescriptor<Clazz>
{
    /**
     * Get the queuename value.
     *
     * @return a <code>String</code> value
     */
    String getQueuename();
}
