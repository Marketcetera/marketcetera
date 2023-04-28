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
     * @param inRawPassword a <code>String</code> value
     * @return a <code>String</code> value
     */
    String getHash(String inRawPassword);
    /**
     * Indicates if the given raw password is equivalent to the given hashed value.
     *
     * @param inRawPassword a <code>String</code> value
     * @param inHashedPassword a <code>String</code> value
     * @return a <code>boolean</code> value
     */
    boolean matches(String inRawPassword,
                    String inHashedPassword);
}
