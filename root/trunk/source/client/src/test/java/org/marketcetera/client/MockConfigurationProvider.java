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
* @since $Release$
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
}
