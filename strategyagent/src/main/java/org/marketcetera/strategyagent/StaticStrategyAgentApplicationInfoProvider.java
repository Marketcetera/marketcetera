package org.marketcetera.strategyagent;

import java.io.File;

import org.marketcetera.core.StaticApplicationInfoProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides application information for the strategy agent.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: StaticStrategyAgentApplicationInfoProvider.java 16901 2014-05-11 16:14:11Z colin $
 * @since 2.4.0
 */
@ClassVersion("$Id: StaticStrategyAgentApplicationInfoProvider.java 16901 2014-05-11 16:14:11Z colin $")
public class StaticStrategyAgentApplicationInfoProvider
        extends StaticApplicationInfoProvider
        implements StrategyAgentApplicationInfoProvider
{
    /* (non-Javadoc)
     * @see org.marketcetera.strategyagent.StrategyAgentApplicationInfoProvider#getModulesDir()
     */
    @Override
    public File getModulesDir()
    {
        return new File(getAppDir(),
                        "modules");
    }
}
