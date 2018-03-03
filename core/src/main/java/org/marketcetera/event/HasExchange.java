package org.marketcetera.event;

/* $License$ */

/**
 * Indicates that the implementer has an exchange value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasExchange
{
    /**
     * Get the exchange value.
     *
     * @return a <code>String</code> value or <code>null</code>
     */
    String getExchange();
}
