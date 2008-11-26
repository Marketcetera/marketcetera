package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.DisplayName;

/* $License$ */
/**
 * Management interface for the Client Module Provider.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@DisplayName("Management Interface for Client Module Provider")  //$NON-NLS-1$
@ClassVersion("$Id$") //$NON-NLS-1$
public interface ClientModuleFactoryMXBean {
    /**
     * The Server URL to connect to.
     *
     * @return the Server URL
     */
    @DisplayName("The Server URL")  //$NON-NLS-1$
    String getURL();

    /**
     * Sets the Server URL to connect to.
     *
     * @param inURL the Server URL
     */
    @DisplayName("The Server URL")  //$NON-NLS-1$
    void setURL(
            @DisplayName("The Server URL")  //$NON-NLS-1$
            String inURL);

    /**
     * The username to use when connecting to the server.
     *
     * @return the username.
     */
    @DisplayName("The Username for connecting to the server")  //$NON-NLS-1$
    String getUsername();

    /**
     * Sets the user name to use when connecting to the server.
     *
     * @param inUsername the username.
     */
    @DisplayName("The Username for connecting to the server")  //$NON-NLS-1$
    void setUsername(
            @DisplayName("The Username for connecting to the server")  //$NON-NLS-1$
            String inUsername);

    /**
     * Sets the password to use when connecting to the server.
     *
     * @param inPassword the password.
     */
    @DisplayName("The Password for connecting to the server")  //$NON-NLS-1$
    void setPassword(String inPassword);
}
