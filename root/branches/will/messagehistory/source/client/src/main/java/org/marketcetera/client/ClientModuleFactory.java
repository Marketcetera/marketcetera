package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;

/* $License$ */
/**
 * The provider / factory that creates Client Module instance.
 * <p>
 * The factory assumes that the Client is already initialized if
 * the URL is not set. Otherwise if the URL is set and the Client is
 * not {@link ClientManager#isInitialized()  initialized},
 * the factory will initialize the Client before
 * creating the module instance.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ClientModuleFactory extends ModuleFactory
        implements ClientModuleFactoryMXBean {
    @Override
    public Module create(Object[] inParameters) throws ModuleCreationException {
        if(getURL() != null && !ClientManager.isInitialized()) {
            ClientParameters parameters = new ClientParameters(getUsername(),
                    getPassword() == null
                    ? null
                    : getPassword().toCharArray(),getURL());
            try {
                ClientManager.init(parameters);
            } catch (ConnectionException e) {
                throw new ModuleCreationException(e,
                        Messages.CREATE_MODULE_ERROR);
            } catch (ClientInitException e) {
                //This failure cannot happen as we only execute
                //this code when client is not initialized.
                throw new ModuleCreationException(e,
                        Messages.CREATE_MODULE_ERROR);
            }
        }
        return new ClientModule(INSTANCE_URN, true);
    }

    /**
     * Creates an instance.
     */
    public ClientModuleFactory() {
        super(PROVIDER_URN, Messages.PROVIDER_DESCRIPTION, false, false);
    }

    @Override
    public String getURL() {
        return mURL;
    }

    @Override
    public void setURL(String inURL) {
        mURL = inURL;
    }

    @Override
    public String getUsername() {
        return mUsername;
    }

    @Override
    public void setUsername(String inUsername) {
        mUsername = inUsername;
    }

    @Override
    public void setPassword(String inPassword) {
        mPassword = inPassword;
    }

    private String getPassword() {
        return mPassword;
    }

    private String mURL;
    private String mUsername;
    private String mPassword;
    static final ModuleURN PROVIDER_URN = new ModuleURN("metc:ors:system");  //$NON-NLS-1$
    public static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN, "single");  //$NON-NLS-1$
}
