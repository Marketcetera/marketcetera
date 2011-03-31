package org.marketcetera.server.service;

import org.marketcetera.strategyagent.StrategyAgentInterface;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides access to the <code>StrategyAgent</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface StrategyAgentManager
{
    /**
     * Gets the <code>StrategyAgent</code> to use to run modules.
     *
     * @return a <code>StrategyAgentInterface</code> value
     */
    public StrategyAgentInterface getStrategyAgent();
}
