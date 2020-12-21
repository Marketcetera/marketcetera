package org.marketcetera.core;

import org.marketcetera.util.log.SLF4JLoggerProxy;

import junit.framework.Test;
import junit.framework.TestCase;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class LoggerStartupTest extends TestCase {
    public LoggerStartupTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(LoggerStartupTest.class);
    }

    /**
     * This is a dummy test, mostly for visual checking of whether or not the logger is initialized correctly.
     *
     * @throws Exception if an unexpected error occurs
     */
    public void testLogSomething() throws Exception {
        new ApplicationBase();

        SLF4JLoggerProxy.info(this, "info message coming through"); //$NON-NLS-1$
        SLF4JLoggerProxy.error(this, "not a real error: testing erro message coming through"); //$NON-NLS-1$
    }
}
