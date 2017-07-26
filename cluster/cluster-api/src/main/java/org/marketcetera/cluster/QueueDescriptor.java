package org.marketcetera.cluster;

/* $License$ */

/**
 * Describes a cluster queue.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: QueueDescriptor.java 16534 2015-02-10 20:17:33Z colin $
 * @since 2.5.0
 */
public class QueueDescriptor<Clazz>
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
    public QueueDescriptor(String inQueuename)
    {
        queuename = inQueuename;
    }
    /**
     * uniquely identifies queue in cluster
     */
    private final String queuename;
}
