package org.marketcetera.strategy;

import java.io.IOException;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.strategy.cpp.CPPStrategy;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
@ClassVersion("$Id:$") //$NON-NLS-1$
public class CPPStrategyExecutor
        extends StrategyExecutor
{
    /**
     * Create a new CPPStrategyExecutor instance.
     *
     * @param inStrategy
     * @throws IOException
     */
    public CPPStrategyExecutor(StrategyMetaData inStrategy)
            throws IOException
    {
        super(inStrategy);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyExecutor#doExecute()
     */
    @Override
    protected IStrategy doExecute()
            throws StrategyExecutionException
    {
        // compile strategy to shared library (.so on Unices and .dll on Windows)
        // for now, just assume it already exists
        System.loadLibrary(getStrategy().getName());
        // construct strategy object
        // TODO make constructor set env and object values in CPP side
        CPPStrategy strategy = new CPPStrategy();
        // make a test call
        strategy.onCallback(null);
        return strategy;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyExecutor#prepareScript(java.lang.String)
     */
    @Override
    protected String prepareScript(String inScript)
    {
        return inScript;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategyExecutor#halt()
     */
    @Override
    public void halt()
    {
        // TODO Auto-generated method stub
    }
}
