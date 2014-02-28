package org.marketcetera.core.resourcepool;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Contains a block of instructions to execute with a given {@link Resource} in a {@link ResourcePool}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface ExecutableBlock<ResourceClazz extends Resource,ReturnClazz>
{
    /**
     * Executes a task using the given resource.
     *
     * @param inResource a <code>ResourceClazz</code> value
     * @return a <code>ReturnClazz</code> value
     * @throws Exception if an error occurs during execution
     */
    public ReturnClazz execute(ResourceClazz inResource)
            throws Exception;
}
