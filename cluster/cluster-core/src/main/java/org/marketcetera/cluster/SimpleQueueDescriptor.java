package org.marketcetera.cluster;

/* $License$ */

/**
 * Describes a cluster queue.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.5.0
 */
public class SimpleQueueDescriptor<Clazz>
        implements QueueDescriptor<Clazz>
{
    /**
     * Get the queuename value.
     *
     * @return a <code>String</code> value
     */
    public String getQueuename()
    {
        return queuename;
    }
    /**
     * Create a new QueueDescriptor instance.
     *
     * @param inQueuename a <code>String</code> value
     */
    public SimpleQueueDescriptor(String inQueuename)
    {
        queuename = inQueuename;
    }
    /**
     * uniquely identifies queue in cluster
     */
    private final String queuename;
}
