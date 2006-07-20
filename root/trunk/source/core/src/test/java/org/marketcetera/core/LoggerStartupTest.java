package org.marketcetera.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Properties;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
public class LoggerStartupTest extends TestCase {
    public LoggerStartupTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(LoggerStartupTest.class);
    }

    /** This is a dummy test, mostly for visual checking of whether or not
     * the logger is initialized correctly.
     * @throws Exception
     */
    public void testLogSomething() throws Exception {
        MyApp app = new MyApp(new Properties());

        LoggerAdapter.info("info message coming through", this);
        LoggerAdapter.error("not a real error: testing error message coming through", this);
    }

    private class MyApp extends ApplicationBase
    {
        public MyApp(Properties inProps) {
            super(inProps);
        }
    }
}
