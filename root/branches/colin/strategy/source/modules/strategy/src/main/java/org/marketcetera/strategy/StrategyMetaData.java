package org.marketcetera.strategy;

import org.marketcetera.core.ClassVersion;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 * Represents a <code>Strategy</code> inside the <code>Strategy Module</code>.
 * 
 * <p>This object contains all the information necessary to register, unregister, and
 * execute a <code>Strategy</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class StrategyMetaData
    implements Lifecycle
{
    private final String mName;
    private final StrategyLanguage mLanguage;
    private final String mScript;
    private final IStrategyExecutor mExecutor;
    private IStrategy mStrategy;
    static StrategyMetaData createStrategyData(String inScriptName,
                                               StrategyLanguage inLanguage,
                                               String inScript)
        throws NoExecutorException
    {
        return new StrategyMetaData(inScriptName,
                                    inLanguage,
                                    inScript);
    }
    private StrategyMetaData(String inScriptName,
                             StrategyLanguage inLanguage,
                             String inScript)
        throws NoExecutorException
    {
        mName = inScriptName;
        mLanguage = inLanguage;
        mScript = inScript;
        try {
            mExecutor = mLanguage.getExecutor(this);
        } catch (Throwable t) {
            throw new NoExecutorException(t);
        }
    }
    final String getName()
    {
        return mName;
    }
    final StrategyLanguage getLanguage()
    {
        return mLanguage;
    }
    final String getScript()
    {
        return mScript;
    }
    IStrategy getStrategy()
    {
        return mStrategy;
    }
    final IStrategyExecutor getExecutor()
    {
        return mExecutor;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getStrategy() == null) ? 0 : getStrategy().hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final StrategyMetaData other = (StrategyMetaData) obj;
        if (getStrategy() == null) {
            if (other.getStrategy() != null)
                return false;
        } else if (!getStrategy().equals(other.getStrategy()))
            return false;
        return true;
    }
    void setStrategy(IStrategy inStrategy)
    {
        mStrategy = inStrategy;
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        if(getStrategy() == null) {
            return false;
        }
        return getStrategy().isRunning();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public void start()
    {
        if(getStrategy() == null) {
            // TODO add a message
            throw new IllegalStateException();
        }
        getStrategy().start();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop()
    {
        if(getStrategy() == null) {
            // TODO add a message
            throw new IllegalStateException();
        }
        getStrategy().stop();
    }
}
