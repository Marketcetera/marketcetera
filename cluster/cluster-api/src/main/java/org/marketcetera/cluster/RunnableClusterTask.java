package org.marketcetera.cluster;

/* $License$ */

/**
 * Provides common behavior for clusterable tasks.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: RunnableClusterTask.java 17131 2017-01-25 16:16:17Z colin $
 * @since 2.5.0
 */
public abstract class RunnableClusterTask
        extends AbstractClusterTask
        implements Runnable
{
    private static final long serialVersionUID = 6017470487914431583L;
}
