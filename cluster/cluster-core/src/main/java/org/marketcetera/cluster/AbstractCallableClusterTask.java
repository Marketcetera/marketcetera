package org.marketcetera.cluster;

/* $License$ */

/**
 * Provides common behavior for tasks that can be called on remote cluster members.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: CallableClusterTask.java 16534 2015-02-10 20:17:33Z colin $
 * @since 2.5.0
 */
public abstract class AbstractCallableClusterTask<Clazz>
        extends AbstractClusterTask
        implements CallableClusterTask<Clazz>
{
    private static final long serialVersionUID = 3062142666366911791L;
}
