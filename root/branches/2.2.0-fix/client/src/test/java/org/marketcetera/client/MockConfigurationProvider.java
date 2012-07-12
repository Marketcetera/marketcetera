package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.ModuleConfigurationProvider;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ModuleException;
/* $License$ */

/**
 * MockConfigurationProvider
*
* @author anshul@marketcetera.com
* @version $Id$
* @since 1.0.0
*/
@ClassVersion("$Id$") //$NON-NLS-1$
class MockConfigurationProvider implements ModuleConfigurationProvider {
    public void setURL(String inURL) {
        mURL = inURL;
    }

    public void setUsername(String inUsername) {
        mUsername = inUsername;
    }

    public void setPassword(String inPassword) {
        mPassword = inPassword;
    }

    public void setHostname(String inHostname) {
        mHostname = inHostname;
    }

    public void setPort(int inPort) {
        mPort = inPort;
    }

    public void setIDPrefix(String inIDPrefix) {
        mIDPrefix = inIDPrefix;
    }

    @Override
    public String getDefaultFor(ModuleURN inURN, String inAttribute)
            throws ModuleException {
        if(ClientModuleFactory.PROVIDER_URN.equals(inURN)) {
            if("URL".equals(inAttribute)) {
                return mURL;
            } else if("Username".equals(inAttribute)) {
                return mUsername;
            } else if("Password".equals(inAttribute)) {
                return mPassword;
            } else if("Hostname".equals(inAttribute)) {
                return mHostname;
            } else if("Port".equals(inAttribute)) {
                return String.valueOf(mPort);
            } else if("IDPrefix".equals(inAttribute)) {
                return mIDPrefix;
            }
        }
        return null;
    }

    public void refresh() throws ModuleException {
        //don't do anything.
    }
    private String mURL;
    private String mUsername;
    private String mPassword;
    private String mHostname;
    private int mPort;
    private String mIDPrefix;
}
