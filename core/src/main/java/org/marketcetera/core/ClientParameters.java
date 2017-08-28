package org.marketcetera.core;

import java.util.Locale;

/* $License$ */

/**
 * Provides parameters for a client to connect.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ClientParameters
{
    /**
     * Get the hostname value.
     *
     * @return a <code>String</code> value
     */
    String getHostname();
    /**
     * Get the port value.
     *
     * @return an <code>int</code> value
     */
    int getPort();
    /**
     * Get the username value.
     *
     * @return a <code>String</code> value
     */
    String getUsername();
    /**
     * Get the password value.
     *
     * @return a <code>String</code> value
     */
    String getPassword();
    /**
     * Get the client locale value.
     *
     * @return a <code>Locale</code> value
     */
    Locale getLocale();
}
