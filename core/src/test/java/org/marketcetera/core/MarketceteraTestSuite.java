package org.marketcetera.core;

import junit.framework.TestSuite;

import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXVersion;

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

    public MarketceteraTestSuite(Class<?> aClass) {
        super(aClass);
        init();
    }

    public void init()
    {
        FIXDataDictionary.initializeDataDictionary(FIXVersion.FIX42.getDataDictionaryURL());
    }
}
