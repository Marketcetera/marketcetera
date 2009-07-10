package org.marketcetera.modules.remote.receiver;

import org.marketcetera.util.misc.ClassVersion;

import javax.security.auth.login.Configuration;
import javax.security.auth.login.AppConfigurationEntry;
import java.util.Collections;
import java.util.HashMap;

/* $License$ */
/**
 * A Configuration that returns the {@link ClientLoginModule} as a
 * required module for the login configuration with the name
 * {@link #REMOTING_LOGIN_DOMAIN} and delegates to another configuration for
 * all other login configuration names.
 * <p>
 * This configuration is defined to be able to programmatically set up
 * JAAS authentication configuration for the broker embedded within
 * the remote receiver module.
 * <p>
 * If automatic JAAS Configuration is
 * {@link ReceiverModuleMXBean#isSkipJAASConfiguration()} skipped, the
 * following login configuration must be specified in the JAAS Configuration
 * for the receiver module to work.
 * <pre>
 * remoting-amq-domain {
 *    org.marketcetera.modules.remote.receiver.ClientLoginModule required;
 * };
 * </pre>
 *
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
class JaasConfiguration extends Configuration {

    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(String inName) {
        if(REMOTING_LOGIN_DOMAIN.equals(inName)) {
            return new AppConfigurationEntry[]{
                    new AppConfigurationEntry(ClientLoginModule.class.getName(),
                            AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                            Collections.unmodifiableMap(new HashMap<String, String>()))
            };
        } else {
            return mConfigDelegate == null
                    ? null
                    : mConfigDelegate.getAppConfigurationEntry(inName);
        }
    }

    /**
     * Sets the configuration to this class, if it's not already set.
     */
    synchronized static void setup() {
        Configuration oldConfig = null;
        try {
            oldConfig = Configuration.getConfiguration();
        } catch (Exception ignore) {
        }
        //Wrap the configuration only if it's not the expected one.
        if(oldConfig == null || oldConfig != INSTANCE) {
            INSTANCE = new JaasConfiguration(oldConfig);
            Configuration.setConfiguration(INSTANCE);
        }
    }
    /**
     * Creates an instance.
     *
     * @param inConfigDelegate the configuration instance that should
     * be delegated to.
     */
    private JaasConfiguration(Configuration inConfigDelegate) {
        mConfigDelegate = inConfigDelegate;
    }
    private static Configuration INSTANCE = null;
    private Configuration mConfigDelegate;
    /**
     * The name of the login configuration that the embedded broker
     * is configured with. This matches the configuration name specified
     * in the remoting_server.xml file.
     */
    static final String REMOTING_LOGIN_DOMAIN = "remoting-amq-domain";  //$NON-NLS-1$
}
