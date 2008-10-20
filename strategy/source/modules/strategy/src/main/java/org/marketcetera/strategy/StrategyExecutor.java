package org.marketcetera.strategy;

import java.io.IOException;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
@ClassVersion("$Id:$") //$NON-NLS-1$
abstract class StrategyExecutor
    implements IStrategyExecutor
{
    private final StrategyMetaData mStrategy;
    private final String mStrategyScript;
    
    protected StrategyExecutor(StrategyMetaData inStrategy)
        throws IOException
    {
        mStrategy = inStrategy;
        mStrategyScript = prepareScript(mStrategy.getScript());
    }
    public final void execute()
        throws StrategyExecutionException
    {
        StrategyMetaData strategy = getStrategy();
        try {
            IStrategy strategyObject = doExecute();
            SLF4JLoggerProxy.debug(this,
                                   "Execute returned strategy object {}",
                                   strategyObject);
            strategy.setStrategy(strategyObject);
            StrategyManager.getInstance().reportStrategyCreation(strategy);
            strategyObject.start();
        } catch (Throwable t) {
            throw new StrategyExecutionException(t);
        }
    }
    protected final StrategyMetaData getStrategy()
    {
        return mStrategy;
    }
    protected final String getStrategyScript()
    {
        return mStrategyScript;
    }
    protected abstract IStrategy doExecute()
        throws StrategyExecutionException;
    protected abstract String prepareScript(String inScript);
}
