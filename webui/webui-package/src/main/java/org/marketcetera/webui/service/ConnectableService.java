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
     * 
     *
     *
     * @param inUsername
     * @param inPassword
     * @param inHostname
     * @param inPort
     * @return
     */
    boolean connect(String inUsername,
                    String inPassword,
                    String inHostname,
                    int inPort);
}
