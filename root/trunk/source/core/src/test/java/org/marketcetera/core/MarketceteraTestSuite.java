package org.marketcetera.core;

import junit.framework.TestSuite;
import org.marketcetera.quickfix.FIXDataDictionaryManager;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class MarketceteraTestSuite extends TestSuite {
    public MarketceteraTestSuite() {
        super();
        init();
    }

    public MarketceteraTestSuite(Class aClass, String string) {
        super(aClass, string);
        init();
    }

    public MarketceteraTestSuite(Class aClass) {
        super(aClass);
        init();
    }

    public MarketceteraTestSuite(String string) {
        super(string);
        init();
    }

    public void init()
    {
        LoggerAdapter.initializeLogger("test");
        try {
            FIXDataDictionaryManager.setFIXVersion(FIXDataDictionaryManager.FIX_4_2_BEGIN_STRING);
            
        } catch (Exception ex) {
            LoggerAdapter.error("Error initializing suite", ex, this);
            System.exit(1);
        }
    }
}
