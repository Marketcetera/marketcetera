package org.marketcetera.strategy;

import static org.marketcetera.strategy.Messages.NO_SUPPORT_FOR_LANGUAGE;

import java.util.Vector;

import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * {@link ExecutionEngine} implementation which uses <a href="http://jakarta.apache.org/bsf">Apache Bean Scriping Framewok</a>
 * to execute scripts.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
class BeanScriptingFrameworkEngine
        implements ExecutionEngine
{
    /**
     * script engine manager
     */
    private static final BSFManager scriptManager = new BSFManager();
    /**
     * the script engine responsible for executing this script
     */
    private static BSFEngine scriptEngine;
    /**
     * the strategy to execute
     */
    private Strategy strategy;
    /**
     * the processed script to execute
     */
    private String processedScript;
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.ExecutionEngine#prepare(org.marketcetera.strategy.Strategy, java.lang.String)
     */
    @Override
    public void prepare(Strategy inStrategy,
                        String inProcessedScript)
            throws StrategyException
    {
        strategy = inStrategy;
        processedScript = inProcessedScript;
        SLF4JLoggerProxy.debug(this,
                               "Preparing {}", //$NON-NLS-1$
                               inStrategy);
        registerScriptEngines();
        String languageString = inStrategy.getLanguage().name();
        try {
            synchronized(scriptManager) {
                if(scriptEngine == null) {
                    scriptEngine = scriptManager.loadScriptingEngine(languageString);
                    SLF4JLoggerProxy.debug(this,
                                           "Initializing engine..."); //$NON-NLS-1$
                    scriptEngine.initialize(scriptManager,
                                            languageString,
                                            new Vector<Object>());
                } else {
                    SLF4JLoggerProxy.debug(this,
                                           "Reusing intialized engine..."); //$NON-NLS-1$
                }
            }
        } catch (BSFException e) {
            throw new StrategyException(e,
                                        new I18NBoundMessage1P(NO_SUPPORT_FOR_LANGUAGE,
                                                               languageString));
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.ExecutionEngine#start()
     */
    @Override
    public Object start()
            throws StrategyException
    {
        try {
            return scriptEngine.eval(strategy.getLanguage().name(),
                                     0,
                                     0,
                                     processedScript);
        } catch (BSFException e) {
            // TODO this likely means a compilation or run-time error - figure out how to get more information out of here
            throw new StrategyException(e);
        }
   }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.ExecutionEngine#stop()
     */
    @Override
    public void stop()
            throws StrategyException
    {
    }
    /**
     * Registers script engines needed for language support.
     *
     * <p>When adding support to this class for a new engine, this method must be modified to support that language
     */
    private static void registerScriptEngines()
    {
        if(!BSFManager.isLanguageRegistered(Language.RUBY.name())) {
            BSFManager.registerScriptingEngine(Language.RUBY.name(),
                                               "org.jruby.javasupport.bsf.JRubyEngine", //$NON-NLS-1$
                                               new String[] { "rb" }); //$NON-NLS-1$
        }
    }
}
