package org.marketcetera.strategy;

import static org.marketcetera.strategy.Messages.INVALID_STRATEGY_SUPERCLASS;

import java.io.IOException;
import java.util.Vector;

import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since $Release$
 */
@ClassVersion("$Id:$") //$NON-NLS-1$
abstract class BSFStrategyExecutor
        extends StrategyExecutor
{
    // TODO is this re-entrant, can we use one for everybody?
    private static final BSFManager sScriptManager = new BSFManager();
    private final BSFEngine mScriptEngine;
    /**
     * Create a new BSFStrategyExecutor instance.
     *
     * @param inStrategy
     * @throws IOException 
     */
    protected BSFStrategyExecutor(StrategyMetaData inStrategy)
        throws IOException
    {
        super(inStrategy);
        String languageString = getStrategy().getLanguage().toString();
        try {
            mScriptEngine = getScriptManager().loadScriptingEngine(languageString);
            mScriptEngine.initialize(getScriptManager(),
                                     languageString,
                                     new Vector<Object>());
        } catch (BSFException e) {
            // TODO need to work this one out
            throw new IllegalStateException(e);
        }
    }
    private BSFManager getScriptManager()
    {
        return sScriptManager;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyExecutor#doExecute()
     */
    @Override
    protected final IStrategy doExecute()
            throws StrategyExecutionException
    {
        try {
            SLF4JLoggerProxy.debug(this,
                                   "Executing {} strategy {}",
                                   getStrategy().getLanguage(),
                                   getStrategy().getName());
            StrategyMetaData strategyMetaData = getStrategy();
            try {
                return (IStrategy)mScriptEngine.eval(strategyMetaData.getLanguage().toString(),
                                                     0,
                                                     0,
                                                     getStrategyScript());
            } catch (ClassCastException e) {
                // this is a problem with the executor wrapper
                throw new StrategyExecutionException(new I18NBoundMessage1P(INVALID_STRATEGY_SUPERCLASS,
                                                                            getStrategy().getName()));
            }
//            // temp code
//            if(strategy != null &&
//               strategy instanceof IJavaStrategy) {
////              strategy.onCallback();
//                return strategy;
//            }
//            // temp code
        } catch (BSFException e) {
            SLF4JLoggerProxy.debug(this,
                                   e,
                                   "Error executing {} strategy {}",
                                   getStrategy().getLanguage(),
                                   getStrategy().getName());
            throw new StrategyExecutionException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategyExecutor#halt()
     */
    @Override
    public void halt()
    {
        mScriptEngine.terminate();
    }
    /**
     * Executes strategy scripts that can be executed as <code>Java</code>.
     * 
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id:$
     * @since $Release$
     */
    @ClassVersion("$Id:$") //$NON-NLS-1$
    static class JavaStrategyExecutor
            extends BSFStrategyExecutor
    {
        static
        {
            BSFManager.registerScriptingEngine(StrategyLanguage.JAVA.toString(),
                                               "bsh.util.BeanShellBSFEngine",
                                               new String[] { "java" });
        }
        /**
         * Create a new JavaStrategyExecutor instance.
         *
         * @param inStrategy a <code>StrategyData</code> value
         * @throws IOException 
         */
        JavaStrategyExecutor(StrategyMetaData inStrategy)
            throws IOException
        {
            super(inStrategy);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.StrategyExecutor#prepareScript(java.lang.String)
         */
        @Override
        protected String prepareScript(String inScript)
        {
            StringBuilder fullScript = new StringBuilder();
            fullScript.append(inScript);
            fullScript.append("\nreturn new ").append(getStrategy().getName()).append("();\n");
            SLF4JLoggerProxy.debug(this,
                                   "Java Script prepared as:\n{}",
                                   fullScript.toString());
            return fullScript.toString();
        }
    }
    static class JRubyStrategyExecutor
        extends BSFStrategyExecutor
    {
        static
        {
            BSFManager.registerScriptingEngine(StrategyLanguage.JRUBY.toString(),
                                               "org.jruby.javasupport.bsf.JRubyEngine",
                                               new String[] { "rb" });
        }
        /**
         * Create a new RubyStrategyExecutor instance.
         *
         * @param inStrategy
         * @throws IOException 
         */
        JRubyStrategyExecutor(StrategyMetaData inStrategy)
            throws IOException
        {
            super(inStrategy);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.strategy.StrategyExecutor#prepareScript(java.lang.String)
         */
        @Override
        protected String prepareScript(String inScript)
        {
            StringBuilder fullScript = new StringBuilder();
            fullScript.append(inScript);
            fullScript.append("\n").append(getStrategy().getName()).append(".new\n");
            return fullScript.toString();
        }
    }
}
