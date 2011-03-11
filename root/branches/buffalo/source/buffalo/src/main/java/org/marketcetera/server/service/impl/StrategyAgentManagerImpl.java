package org.marketcetera.server.service.impl;

import org.marketcetera.server.service.StrategyAgentManager;
import org.marketcetera.strategyagent.StrategyAgentInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class StrategyAgentManagerImpl
        implements StrategyAgentManager
{
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.StrategyAgentManager#getStrategyAgent()
     */
    @Override
    public StrategyAgentInterface getStrategyAgent()
    {
        return strategyAgent;
    }
    /**
     * the strategy agent instance to make available
     */
    @Autowired
    private StrategyAgentInterface strategyAgent;
}
