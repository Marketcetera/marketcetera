package org.marketcetera.core;

import junit.framework.TestSuite;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXVersion;

import org.marketcetera.util.log.SLF4JLoggerProxy;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MarketceteraTestSuite extends TestSuite {
    public MarketceteraTestSuite() {
        super();
        init(null);
    }

    public MarketceteraTestSuite(Class aClass) {
        super(aClass);
        init(null);
    }

    public MarketceteraTestSuite(Class aClass, MessageBundleInfo extraBundle) {
        super(aClass);
        init(new MessageBundleInfo[]{extraBundle});
    }

    public MarketceteraTestSuite(Class aClass, MessageBundleInfo[] extraBundles) {
        super(aClass);
        init(extraBundles);
    }

    public void init(MessageBundleInfo[] inBundles)
    {
        if (inBundles != null){
            for (MessageBundleInfo messageBundleInfo : inBundles) {
                MessageBundleManager.registerMessageBundle(messageBundleInfo);
            }
        }
        MessageBundleManager.registerCoreMessageBundle();
        try {
            FIXDataDictionary.initializeDataDictionary(FIXVersion.FIX42.getDataDictionaryURL());
        } catch (Exception ex) {
            SLF4JLoggerProxy.error(this, ex, "Error initializing suite"); //$NON-NLS-1$
            System.exit(1);
        }
    }
}
