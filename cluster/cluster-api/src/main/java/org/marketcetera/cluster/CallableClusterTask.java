package org.marketcetera.cluster;

import java.util.concurrent.Callable;

/* $License$ */

/**
 * Provides a <code>Callable</code> task that can be invoked on cluster members.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface CallableClusterTask<Clazz>
        extends ClusterTask,Callable<Clazz>
{
}
