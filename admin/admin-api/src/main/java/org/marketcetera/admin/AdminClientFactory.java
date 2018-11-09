package org.marketcetera.admin;

import java.util.Locale;

/* $License$ */

/**
 * Create {@link AdminClient} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface AdminClientFactory
{
    /**
     * Create an <code>AdminClient</code> value
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @param inHostname a <code>String</code> value
     * @param inPort an <code>int</code> value
     * @return an <code>AdminClient</code> value
     */
    AdminClient create(String inUsername,
                       String inPassword,
                       String inHostname,
                       int inPort);
    /**
     * Create an <code>AdminClient</code> value
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @param inHostname a <code>String</code> value
     * @param inPort an <code>int</code> value
     * @param inLocale a <code>Locale</code> value
     * @return an <code>AdminClient</code> value
     */
    AdminClient create(String inUsername,
                       String inPassword,
                       String inHostname,
                       int inPort,
                       Locale inLocale);
}
