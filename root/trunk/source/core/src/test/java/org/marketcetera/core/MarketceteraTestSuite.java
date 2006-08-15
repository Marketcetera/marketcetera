package org.marketcetera.core;

import junit.framework.TestSuite;
import org.marketcetera.quickfix.FIXDataDictionaryManager;

import java.util.Arrays;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class MarketceteraTestSuite extends TestSuite {
    private static final MessageBundleInfo CORE_BUNDLE = ApplicationBase.MESSAGE_BUNDLE_INFO;
    public MarketceteraTestSuite() {
        super();
        init(new MessageBundleInfo[]{CORE_BUNDLE});
    }

    public MarketceteraTestSuite(Class aClass) {
        super(aClass);
        init(new MessageBundleInfo[]{CORE_BUNDLE});
    }

    public MarketceteraTestSuite(Class aClass, MessageBundleInfo extraBundle) {
        super(aClass);
        init(new MessageBundleInfo[]{CORE_BUNDLE, extraBundle});
    }

    public MarketceteraTestSuite(Class aClass, MessageBundleInfo[] extraBundles) {
        super(aClass);
        init(Arrays.asList(extraBundles,  CORE_BUNDLE).toArray(new MessageBundleInfo[0]));
    }

    public void init(MessageBundleInfo[] inBundles)
    {
        ApplicationBase.registerMessageBundles(Arrays.asList(inBundles));
        LoggerAdapter.initializeLogger("test");
        try {
            FIXDataDictionaryManager.setFIXVersion(FIXDataDictionaryManager.FIX_4_2_BEGIN_STRING);
        } catch (Exception ex) {
            LoggerAdapter.error("Error initializing suite", ex, this);
            System.exit(1);
        }
    }
}
