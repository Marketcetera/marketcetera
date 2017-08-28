package org.marketcetera.core;

import java.util.Locale;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ClientParameters
{
    /**
     * 
     *
     *
     * @return
     */
    String getHostname();
    /**
     * 
     *
     *
     * @return
     */
    int getPort();
    /**
     * 
     *
     *
     * @return
     */
    String getUsername();
    /**
     * 
     *
     *
     * @return
     */
    String getPassword();
    /**
     * 
     *
     *
     * @return
     */
    Locale getLocale();
}
