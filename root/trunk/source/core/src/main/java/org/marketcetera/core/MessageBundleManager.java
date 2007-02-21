package org.marketcetera.core;

import org.apache.commons.i18n.MessageManager;
import org.apache.commons.i18n.ResourceBundleMessageProvider;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$")
public class MessageBundleManager {
    private static final String CORE_PROVIDER_ID = "core";
    private static final String CORE_BUNDLE_NAME = "core_messages";


    public static void registerMessageBundle(String providerID, String bundleName)
    {
        MessageManager.addMessageProvider(providerID, new ResourceBundleMessageProvider(bundleName));
    }

    public static void registerMessageBundle(MessageBundleInfo info)
    {
        registerMessageBundle(info.getProviderID(), info.getBundleName());
    }

    public static void registerCoreMessageBundle(){
        registerMessageBundle(CORE_PROVIDER_ID, CORE_BUNDLE_NAME);
    }
}
