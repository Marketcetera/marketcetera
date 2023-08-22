package org.marketcetera.ui.service;

/* $License$ */

/**
 * Provides a service that requires a connection.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ConnectableService
{
    /**
     * Connect the service with the given parameters.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @param inHostname a <code>String</code> value
     * @param inPort an <code>int</code> value
     * @param inUseSsl a <code>boolean</code> value
     * @return a <code>boolean</code> value indicating if connection was successful
     * @throws Exception if an error occurs
     */
    boolean connect(String inUsername,
                    String inPassword,
                    String inHostname,
                    int inPort,
                    boolean inUseSsl)
            throws Exception;
    /**
     * Disconnect the service.
     */
    void disconnect();
    /**
     * Indicate if the service is running or not.
     *
     * @return a <code>boolean</code> value
     */
    boolean isRunning();
}
