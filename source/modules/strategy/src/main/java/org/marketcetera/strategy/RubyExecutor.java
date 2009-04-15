package org.marketcetera.strategy;

import java.util.HashMap;
import java.util.Map;

import org.apache.bsf.BSFException;
import org.jruby.exceptions.RaiseException;
import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * {@link Executor} implementation for Ruby strategies.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
class RubyExecutor
    extends AbstractExecutor
{
    /**
     * Create a new RubyExecutor instance.
     *
     * @param inStrategy a <code>Strategy</code> value
     */
    RubyExecutor(Strategy inStrategy)
    {
        super(inStrategy);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.Executor#interpretRuntimeException(java.lang.Exception)
     */
    @Override
    public String interpretRuntimeException(Exception inE)
    {
        return exceptionAsString(inE);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.AbstractExecutor#preprocess(java.lang.String)
     */
    @Override
    protected String preprocess(String inScript)
            throws StrategyException
    {
        // in order to return a RunningStrategy object, it is necessary to tack on a "new" call to the end of the script
        // this is why we need to know the name of the class that the user intends to be the main class of the strategy
        StringBuilder fullScript = new StringBuilder();
        fullScript.append(inScript);
        fullScript.append("\n").append(getStrategy().getName()).append(".new\n"); //$NON-NLS-1$  //$NON-NLS-2$
        String processedScript = fullScript.toString();
        return processedScript;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.AbstractExecutor#getExecutionEngine()
     */
    @Override
    protected ExecutionEngine getExecutionEngine()
            throws StrategyException
    {
        return new BeanScriptingFrameworkEngine();
    }
    /**
     * Attempts to interpret the given exception in a Ruby context.
     *
     * @param inE an <code>Exception</code>
     * @return a <code>String</code> describing the exception's contents
     */
    static String exceptionAsString(Exception inE)
    {
        RaiseException raiseException = null;
        if(inE instanceof RaiseException) {
            raiseException = (RaiseException)inE;
        } else if(inE instanceof BSFException &&
                 ((BSFException)inE).getTargetException() instanceof RaiseException) {
            raiseException = (RaiseException)((BSFException)inE).getTargetException();
        }
        if(raiseException != null) {
            return String.format("%s %s", //$NON-NLS-1$
                                 raiseException.getException().toString(),
                                 raiseException.getException().backtrace().toString());
        }
        return inE.toString();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.Executor#translateMethodName(java.lang.String)
     */
    @Override
    public String translateMethodName(String inMethodName)
    {
        return methodNames.get(inMethodName);
    }
    /**
     * method name translation
     */
    private static final Map<String,String> methodNames = new HashMap<String,String>();
    /**
     * static initialized for class
     */
    static
    {
        methodNames.put("onAsk", //$NON-NLS-1$
                        "on_ask"); //$NON-NLS-1$
        methodNames.put("onBid", //$NON-NLS-1$
                        "on_bid"); //$NON-NLS-1$
        methodNames.put("onMarketstat", //$NON-NLS-1$
                        "on_marketstat"); //$NON-NLS-1$
        methodNames.put("onCancelReject", //$NON-NLS-1$
                        "on_cancel_reject"); //$NON-NLS-1$
        methodNames.put("onExecutionReport", //$NON-NLS-1$
                        "on_execution_report"); //$NON-NLS-1$
        methodNames.put("onTrade", //$NON-NLS-1$
                        "on_trade"); //$NON-NLS-1$
        methodNames.put("onOther", //$NON-NLS-1$
                        "on_other"); //$NON-NLS-1$
        methodNames.put("onStart", //$NON-NLS-1$
                        "on_start"); //$NON-NLS-1$
        methodNames.put("onStop", //$NON-NLS-1$
                        "on_stop"); //$NON-NLS-1$
    }
}
