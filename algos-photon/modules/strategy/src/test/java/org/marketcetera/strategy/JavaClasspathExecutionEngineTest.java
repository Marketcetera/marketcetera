package org.marketcetera.strategy;

import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Tests {@link JavaClasspathExecutionEngine}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class JavaClasspathExecutionEngineTest
        extends StrategyTestBase
{
    /**
     * Tests constructing java stategies from the classpath instead of with the compiler.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testRunFromClassloader()
            throws Exception
    {
        // valid strategy located in classpath
        doStart(ClasspathStrategy.class.getCanonicalName());
        // non-existent strategy
        new ExpectedFailure<ModuleException>() {
            @Override
            protected void run()
                    throws Exception
            {
                doStart("this isn't a valid class");
            }
        };
        // not a strategy
        new ExpectedFailure<ModuleException>() {
            @Override
            protected void run()
                    throws Exception
            {
                doStart(JavaClasspathExecutionEngineTest.this.getClass().getCanonicalName());
            }
        };
        // valid strategy, but incomplete full name (no package)
        new ExpectedFailure<ModuleException>() {
            @Override
            protected void run()
                    throws Exception
            {
                doStart(ClasspathStrategy.class.getSimpleName());
            }
        };
    }
    /**
     * Tests create, start, stop, and delete of the given strategy class name.
     *
     * @param inStrategyClassname a <code>String</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doStart(String inStrategyClassname)
            throws Exception
    {
        SLF4JLoggerProxy.debug(this,
                               "Starting strategy: {}",
                               inStrategyClassname);
        ModuleURN strategy = moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                                        "MyStategy",
                                                        inStrategyClassname,
                                                        Language.JAVA,
                                                        null,
                                                        null,
                                                        false,
                                                        null);
        moduleManager.start(strategy);
        moduleManager.stop(strategy);
        moduleManager.deleteModule(strategy);
    }
}
