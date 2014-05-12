package org.marketcetera.strategyagent;

import java.io.File;

import org.marketcetera.core.ApplicationInfoProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides application information for the strategy agent.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public interface StrategyAgentApplicationInfoProvider
        extends ApplicationInfoProvider
{
    /**
     * Gets the SA modules dir value.
     *
     * @return a <code>File</code> value
     */
    public File getModulesDir();
}
