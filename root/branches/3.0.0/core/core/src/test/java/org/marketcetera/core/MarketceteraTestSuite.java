package org.marketcetera.core;

import junit.framework.TestSuite;

import org.junit.Ignore;
import org.marketcetera.core.quickfix.FIXDataDictionary;
import org.marketcetera.core.quickfix.FIXVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/**
 * @author Toli Kuznets
 * @version $Id: MarketceteraTestSuite.java 16063 2012-01-31 18:21:55Z colin $
 */
@Ignore
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
        try {
            FIXDataDictionary.initializeDataDictionary(FIXVersion.FIX42.getDataDictionaryURL());
        } catch (Exception ex) {
            SLF4JLoggerProxy.error(this, ex, "Error initializing suite"); //$NON-NLS-1$
            System.exit(1);
        }
    }
}
