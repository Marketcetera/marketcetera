package org.marketcetera.webui.service;

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
     * Connects to the service.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @param inHostname a <code>String</code> value
     * @param inPort an <code>int</code> value
     * @return a <code>boolean</code> value
     * @throws Exception if an error occurs connecting to the service
     */
    boolean connect(String inUsername,
                    String inPassword,
                    String inHostname,
                    int inPort)
            throws Exception;
}
