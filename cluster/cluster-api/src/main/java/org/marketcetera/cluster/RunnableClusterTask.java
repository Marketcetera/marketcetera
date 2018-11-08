package org.marketcetera.cluster;

/* $License$ */

/**
 * Provides a <code>Runnable</code> task that can be invoked on cluster members.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface RunnableClusterTask
        extends ClusterTask,Runnable
{
}
