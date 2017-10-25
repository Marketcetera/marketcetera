package org.marketcetera.admin.service;

/* $License$ */

/**
 * Provides password services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface PasswordService
{
    /**
     * Get the hash of the given value.
     *
     * @param inValue a <code>String</code> value
     * @return a <code>String</code> value
     */
    String getHash(String inValue);
    /**
     * Get the hash of the given value.
     *
     * @param inValue a <code>char[][]</code> value
     * @return a <code>String</code> value
     */
    String getHash(char[]...inValues);
}
