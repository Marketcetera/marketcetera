package org.marketcetera.modules.remote.emitter;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.DisplayName;

import javax.management.MXBean;

/* $License$ */
/**
 * The management interface for the EmitterModule.
 * <p>
 * The implementation of this interface emits
 * {@link javax.management.AttributeChangeNotification} whenever the value
 * of {@link #isConnected()} attribute changes.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
@MXBean(true)
@DisplayName("Management Interface for Remote Emitter Module")
public interface EmitterModuleMXBean {
    /**
     * Gets the URL for connecting to the remote receiver module.
     *
     * @return the URL value.
     */
    @DisplayName("The URL to connect to Remote Receiver")
    public String getURL();

    /**
     * Sets the URL for connecting to the remote receiver module.
     *
     * @param inURL the URL value.
     *
     * @throws IllegalStateException if the module is started. The URL
     * can only be set when the module is not started.
     */
    @DisplayName("The URL to connect to Remote Receiver")
    public void setURL(
            @DisplayName("The URL to connect to Remote Receiver")
            String inURL);

    /**
     * Gets the user name to use to authenticate when connecting to the
     * remote receiver module.
     *
     * @return the user name.
     */
    @DisplayName("The User Name")
    public String getUsername();

    /**
     * Sets the user name to use to authenticate when connecting to the
     * remote receiver module.
     *
     * @param inUsername the user name.
     *
     * @throws IllegalStateException if the module is started. The Username
     * can only be set when the module is not started.
     */
    @DisplayName("The User Name")
    public void setUsername(
            @DisplayName("The User Name")
            String inUsername);

    /**
     * Sets the password to use to authenticate when connecting to the
     * remote receiver module.
     *
     * @param inPassword the password.
     *
     * @throws IllegalStateException if the module is started. The password
     * can only be set when the module is not started.
     */
    @DisplayName("The Password")
    public void setPassword(
            @DisplayName("The Password")
            String inPassword);

    /**
     * Returns true if the connection to the remote receiver is active.
     * <p>
     * The bean emits attribute change notifications when this
     * attribute value changes.
     *
     * @return true if the connection to the remote receiver is active.
     */
    @DisplayName("Whether Connection to the remove receiver is active")
    public boolean isConnected();

    /**
     * Returns the last failure encountered when receiving objects.
     *
     * @return last failure encountered. Null, if no failures have
     * been encountered so far.
     */
    @DisplayName("The last failure encountered when receiving objects, if any.")
    public String getLastFailure();
}
