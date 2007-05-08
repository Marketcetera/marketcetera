package org.marketcetera.oms;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ConfigFileLoadingException;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import quickfix.field.Symbol;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class OMSStartupTest extends TestCase {

    private boolean failed = false;
    private Exception failureEx;

    public OMSStartupTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new TestSuite(OMSStartupTest.class);
    }

    /** test the startup of the real OMS appContext, sleeps for 10 secs and exits
     * Really, we just care to check that the spring config is setup correctly, nothing else.
     */
    public void testRealOMSStartup() throws Exception {
        failed = false;
        (new Thread(new Runnable() {
            public void run() {
                try {
                    OrderManagementSystem.main(new String[0]);
                } catch (ConfigFileLoadingException e) {
                    failed = true;
                    failureEx = e;
                }
            }
        })).start();
        Thread.sleep(10000);

        assertFalse("failure in OMS startup: " + failureEx, failed);

        // call through to the FIXDataDictionary in a way that doesn't explicitly load the dictionary
        assertNotNull("fix dictionary not initialized",
                FIXDataDictionaryManager.getCurrentFIXDataDictionary().getHumanFieldName(Symbol.FIELD));
        assertEquals("wrong fix version: " + FIXDataDictionaryManager.getCurrentFIXDataDictionary().getDictionary().getVersion(),
                FIXDataDictionary.FIX_4_2_BEGIN_STRING,
                FIXDataDictionaryManager.getCurrentFIXDataDictionary().getDictionary().getVersion());
    }

}
