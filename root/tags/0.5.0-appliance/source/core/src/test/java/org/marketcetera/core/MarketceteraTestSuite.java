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
        LoggerAdapter.initializeLogger("test");
        try {
            FIXDataDictionary.initializeDataDictionary(FIXVersion.FIX42.getDataDictionaryURL());
        } catch (Exception ex) {
            LoggerAdapter.error("Error initializing suite", ex, this);
            System.exit(1);
        }
    }
}
