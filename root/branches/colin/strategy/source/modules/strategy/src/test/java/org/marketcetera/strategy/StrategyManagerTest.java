package org.marketcetera.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.strategy.Messages.INVALID_STRATEGY_SUPERCLASS;
import static org.marketcetera.strategy.Messages.STRATEGY_ALREADY_REGISTERED;
import static org.marketcetera.strategy.Messages.STRATEGY_NOT_FOUND;

import java.io.File;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.persist.PersistTestBase;
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
public class StrategyManagerTest
{
    private StrategyManager strategyManager;
    private File javaStrategy;
    @BeforeClass
    public static void once()
    {
        logSetup();
    }
    @Before
    public void beforeEachTest()
        throws Exception
    {
        strategyManager = StrategyManager.getInstance();
        Set<String> strategies = strategyManager.getRegisteredStrategyNames();
        for(String registeredStrategy : strategies) {
            strategyManager.unregister(registeredStrategy);
        }
        javaStrategy = new File(StrategyTypeTestBase.SAMPLE_STRATEGY_DIR,
                                JavaStrategyTest.STRATEGY_FILENAME);
    }
    @Test
    public void testRegister()
        throws Exception
    {
        /*
         x null name
         x non-null name
         x type is valid
         x type is not valid
         x stream is null
         x stream is non-null
         x stream is empty
         x stream is not empty but not valid
         x stream is not empty and valid
         x stream is of matching type
         x stream is not of matching type
         x strategy exists
         x strategy does not exist
         x strategy does not extend Strategy
         x strategy extends Strategy
         */
        assertTrue(strategyManager.getRegisteredStrategyNames().isEmpty());
        // null name
        new ExpectedTestFailure(NullPointerException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                strategyManager.register(null,
                                         StrategyLanguage.JAVA.ordinal(),
                                         javaStrategy);
            }
        }.run();
        assertTrue(strategyManager.getRegisteredStrategyNames().isEmpty());
        // invalid type
        new ExpectedTestFailure(IllegalArgumentException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                strategyManager.register("someName",
                                         -1,
                                         javaStrategy);
            }
        }.run();
        assertTrue(strategyManager.getRegisteredStrategyNames().isEmpty());
        // invalid type
        new ExpectedTestFailure(IllegalArgumentException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                strategyManager.register("someName",
                                         Integer.MIN_VALUE,
                                         javaStrategy);
            }
        }.run();
        assertTrue(strategyManager.getRegisteredStrategyNames().isEmpty());
        // invalid type
        new ExpectedTestFailure(IllegalArgumentException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                strategyManager.register("someName",
                                         Integer.MAX_VALUE,
                                         javaStrategy);
            }
        }.run();
        assertTrue(strategyManager.getRegisteredStrategyNames().isEmpty());
        // null script
        new ExpectedTestFailure(NullPointerException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                strategyManager.register("someName",
                                         StrategyLanguage.JAVA.ordinal(),
                                         null);
            }
        }.run();
        assertTrue(strategyManager.getRegisteredStrategyNames().isEmpty());
        // this strategy registers correctly
        strategyManager.register(JavaStrategyTest.STRATEGY_NAME,
                                 StrategyLanguage.JAVA.ordinal(),
                                 javaStrategy);
        // now, there is a strategy registered
        assertEquals(1,
                     strategyManager.getRegisteredStrategyNames().size());
        assertTrue(strategyManager.getRegisteredStrategyNames().contains(JavaStrategyTest.STRATEGY_NAME));
        // try to register a strategy by the same name
        new ExpectedTestFailure(StrategyAlreadyRegistedException.class,
                                new I18NBoundMessage1P(STRATEGY_ALREADY_REGISTERED,
                                                       JavaStrategyTest.STRATEGY_NAME).getText()) {
            @Override
            protected void execute()
                    throws Throwable
            {
                strategyManager.register(JavaStrategyTest.STRATEGY_NAME,
                                         StrategyLanguage.JAVA.ordinal(),
                                         javaStrategy);
            }
        }.run();
        // there is still a strategy registered
        assertEquals(1,
                     strategyManager.getRegisteredStrategyNames().size());
        assertTrue(strategyManager.getRegisteredStrategyNames().contains(JavaStrategyTest.STRATEGY_NAME));
    }
    @Test
    public void testUnregister()
        throws Exception
    {
        /*
         x strategy registered
         x strategy not registered
         x strategy running
         x strategy not running
         x name null
         x name not null
         */
        assertTrue(strategyManager.getRegisteredStrategyNames().isEmpty());
        // null name
        new ExpectedTestFailure(NullPointerException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                strategyManager.unregister(null);
            }
        }.run();
        // unregister a strategy that's not registered
        new ExpectedTestFailure(StrategyNotFoundException.class,
                                new I18NBoundMessage1P(STRATEGY_NOT_FOUND,
                                                       "BadStrategy").getText()) {
            @Override
            protected void execute()
                    throws Throwable
            {
                strategyManager.unregister("BadStrategy");
            }
        }.run();
        // unregister a registered strategy
        assertTrue(strategyManager.getRegisteredStrategyNames().isEmpty());
        strategyManager.register(JavaStrategyTest.STRATEGY_NAME,
                                 StrategyLanguage.JAVA.ordinal(),
                                 javaStrategy);
        assertEquals(1,
                     strategyManager.getRegisteredStrategyNames().size());
        assertTrue(strategyManager.getRegisteredStrategyNames().contains(JavaStrategyTest.STRATEGY_NAME));
        strategyManager.unregister(JavaStrategyTest.STRATEGY_NAME);
        assertTrue(strategyManager.getRegisteredStrategyNames().isEmpty());
        // unregister a running strategy
        strategyManager.register(JavaStrategyTest.STRATEGY_NAME,
                                 StrategyLanguage.JAVA.ordinal(),
                                 javaStrategy);
        assertEquals(1,
                     strategyManager.getRegisteredStrategyNames().size());
        assertTrue(strategyManager.getRegisteredStrategyNames().contains(JavaStrategyTest.STRATEGY_NAME));
        strategyManager.execute(JavaStrategyTest.STRATEGY_NAME);
        Set<StrategyMetaData> strategies = strategyManager.getRunningStrategies();
        assertEquals(1,
                     strategies.size());
        StrategyMetaData myClassData = strategies.iterator().next();
        assertEquals(JavaStrategyTest.STRATEGY_NAME,
                     myClassData.getName());
        myClassData.stop();
        assertTrue(strategyManager.getRunningStrategies().isEmpty());
        strategyManager.unregister(myClassData.getName());
        assertTrue(strategyManager.getRegisteredStrategyNames().isEmpty());
    }
    @Test
    public void testExecute()
        throws Exception
    {
        // empty script - does not compile
        new ExpectedTestFailure(StrategyExecutionException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                strategyManager.register("EmptyScript",
                                         StrategyLanguage.JAVA.ordinal(),
                                         new File(StrategyTypeTestBase.SAMPLE_STRATEGY_DIR,
                                                  "EmptyJavaStrategy.java"));
                strategyManager.execute("EmptyScript");
            }
        }.run();
        assertTrue(strategyManager.getRunningStrategies().isEmpty());
        // invalid strategy - does not compile with given type
        new ExpectedTestFailure(StrategyExecutionException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                strategyManager.register(JavaStrategyTest.BAD_STRATEGY_NAME,
                                         StrategyLanguage.JAVA.ordinal(),
                                         new File(StrategyTypeTestBase.SAMPLE_STRATEGY_DIR,
                                                  JavaStrategyTest.BAD_STRATEGY_FILENAME));
                strategyManager.execute(JavaStrategyTest.BAD_STRATEGY_NAME);
            }
        }.run();
        assertTrue(strategyManager.getRunningStrategies().isEmpty());
        // valid strategy, but mis-matched with the type
        new ExpectedTestFailure(StrategyExecutionException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                strategyManager.register(JavaStrategyTest.STRATEGY_NAME,
                                         StrategyLanguage.JRUBY.ordinal(),
                                         javaStrategy);
                strategyManager.execute(JavaStrategyTest.STRATEGY_NAME);
            }
        }.run();
        assertTrue(strategyManager.getRunningStrategies().isEmpty());
        // try to execute a strategy that does not extend the class Strategy
        new ExpectedTestFailure(StrategyExecutionException.class,
                                new I18NBoundMessage1P(INVALID_STRATEGY_SUPERCLASS,
                                                       "BadStrategy").getText()) {
            @Override
            protected void execute()
                    throws Throwable
            {
                strategyManager.register("WrongClassJavaStrategy",
                                         StrategyLanguage.JAVA.ordinal(),
                                         new File(StrategyTypeTestBase.SAMPLE_STRATEGY_DIR,
                                                  "WrongClassJavaStrategy.java"));
                strategyManager.execute("WrongClassJavaStrategy");
            }
        }.run();
        assertTrue(strategyManager.getRunningStrategies().isEmpty());
    }
    @Test
    public void testStrategy()
        throws Exception
    {
//        String javaScript = "public void onCallback() { System.out.println(\"Java received onCallback\");}";
//        String javaScript = "import org.marketcetera.strategy.Strategy;\n" +
//                            "import org.marketcetera.strategy.IStrategy;\n" +
//                            "// this is a comment\n" +
//                            "public class MyClass extends Strategy\n" +
//                            "{\n" +
//                            "/*\n" +
//                            "  this should be ignored as well" +
//                            "*/ final int thisShouldNotBeIgnored = 1; // ignore the last of this line\n" +
//        		            "    public void onCallback() { System.out.println(\"Java received onCallback\");}\n" +
//                            "}\n";
//        StrategyData javaScriptData = new StrategyData("MyClass",
//                                                       new ByteArrayInputStream(javaScript.getBytes()),
//                                                       StrategyLanguage.JAVA,
//                                                       new IStrategyMonitor() {
//            @Override
//            public void onFailed(Throwable inT)
//            {
//                inT.printStackTrace();
//            }
//            @Override
//            public void statusChange(Status inStatus)
//            {
//                System.out.println("Java status changed to " + inStatus);
//            }
//        });
////        sm.execute(javaScriptData);
//        
//        StringBuilder rubyScript = new StringBuilder(); 
//        rubyScript.append("require 'java'\n");
//        rubyScript.append("module Marketcetera\n");
//        rubyScript.append("  include_class \"org.marketcetera.strategy.Strategy\"\n");
//        rubyScript.append("end\n");
//        rubyScript.append("class MyStrategy < Marketcetera::Strategy\n");
//        rubyScript.append("def onCallback()\n  puts \"Ruby received onCallback at \" + getCurrentTime().toString()\nend\n");
//        rubyScript.append("end\n");
//        StrategyData rubyScriptData = new StrategyData("MyStrategy",
//                                                       new ByteArrayInputStream(rubyScript.toString().getBytes()),
//                                                       StrategyLanguage.JRUBY,
//                                                       new IStrategyMonitor() {
//            @Override
//            public void onFailed(Throwable inT)
//            {
//                inT.printStackTrace();
//            }
//            @Override
//            public void statusChange(Status inStatus)
//            {
//                System.out.println("JRuby status changed to " + inStatus);
//            }
//        });
////        sm.execute(rubyScriptData);
//        Thread.sleep(10000);
    }
    public static final File TEST_ROOT = new File("src" +
                                                  File.separator + "test");
    public static final File TEST_SAMPLE_DATA = new File(TEST_ROOT,
                                                         "sample_data");
    public static final File TEST_CONF = new File(TEST_SAMPLE_DATA,
                                                  "conf");
    public static final File LOGGER_CONFIG = new File(TEST_CONF,
                                                      "log4j.properties");
    protected static void logSetup()
    {
        if(!LOGGER_CONFIG.exists()) {
            SLF4JLoggerProxy.warn(PersistTestBase.class,
                                  "logger configuration file {} not found", 
                                  LOGGER_CONFIG.getAbsolutePath());
        }
        PropertyConfigurator.configureAndWatch
        (LOGGER_CONFIG.getAbsolutePath(), 10 * 1000l); //10 seconds
    }
}
