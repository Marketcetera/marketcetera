package org.marketcetera.ors;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ConfigFileLoadingException;
import org.marketcetera.ors.OrderRoutingSystem;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.CurrentFIXDataDictionary;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import quickfix.field.Symbol;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class ORSStartupTest extends TestCase {

    private boolean failed = false;
    private Exception failureEx;

    public ORSStartupTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new TestSuite(ORSStartupTest.class);
    }

    /** test the startup of the real ORS appContext, sleeps for 10 secs and exits
     * Really, we just care to check that the spring config is setup correctly, nothing else.
     */
    public void testRealORSStartup() throws Exception {
        failed = false;
        OrderRoutingSystem theORS=new OrderRoutingSystem(new String[0]);

        assertFalse("failure in ORS startup: " + failureEx, failed); //$NON-NLS-1$

        // call through to the FIXDataDictionary in a way that doesn't explicitly load the dictionary
        /*
        assertNotNull("fix dictionary not initialized", //$NON-NLS-1$
                CurrentFIXDataDictionary.getCurrentFIXDataDictionary().getHumanFieldName(Symbol.FIELD));
        assertEquals("wrong fix version: " + CurrentFIXDataDictionary.getCurrentFIXDataDictionary().getDictionary().getVersion(), //$NON-NLS-1$
                FIXDataDictionary.FIX_4_2_BEGIN_STRING,
                CurrentFIXDataDictionary.getCurrentFIXDataDictionary().getDictionary().getVersion());
        */
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DBInit.initORSDB();
    }
}
