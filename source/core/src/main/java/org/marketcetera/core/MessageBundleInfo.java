package org.marketcetera.core;

/**
 * Simple struct to hold the id and the file name for a message bundle name used for internationalization.
 * Each subproject (ie each subclass of {@link ApplicationBase} should have one of these to add
 * to all the default bundles.
 * This struct should be used for calls to {@link org.apache.commons.i18n.MessageManager#addMessageProvider}.
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$") //$NON-NLS-1$
public class MessageBundleInfo {

    private String providerID;
    private String bundleName;

    public MessageBundleInfo(String providerID, String bundleName) {
        this.providerID = providerID;
        this.bundleName = bundleName;
    }

    public String getProviderID() {
        return providerID;
    }

    public String getBundleName() {
        return bundleName;
    }
}
