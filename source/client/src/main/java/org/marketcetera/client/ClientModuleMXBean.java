package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.DisplayName;

import javax.management.MXBean;
import java.util.Date;

/* $License$ */
/**
 * Management interface for the Client Module.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@MXBean(true)
@DisplayName("Management interface for the Client Module instance")
@ClassVersion("$Id$") //$NON-NLS-1$
public interface ClientModuleMXBean {
    /**
     * Reconnects the client to the server.
     *
     * @throws RuntimeException if there were errors while reconnecting.
     */
    @DisplayName("Reconnects the client module to the server")
    public void reconnect() throws RuntimeException;

    /**
     * Returns the parameters used by the client to connect to the server.
     *
     * @return the parameters used by the client to connect to the server.
     * 
     * @throws RuntimeException if there errors fetching the parameters.
     */
    @DisplayName("Parameters used by the client to connect to the server")
    public ClientParameters getParameters() throws RuntimeException;

    /**
     * Returns the time the client was connected or reconnected to the server.
     *
     * @return the time, client was connected to reconnected.
     *
     * @throws RuntimeException if the client was not initialized.
     */
    @DisplayName("The last time this module was connected to the server")
    public Date getLastConnectTime() throws RuntimeException;
}
