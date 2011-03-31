package org.marketcetera.server.security;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface PasswordManager
{
    /**
     * 
     *
     *
     * @param inPassword
     * @param inData
     * @return
     */
    public String encodePassword(String inPassword,
                                 Object inData);
    /**
     * 
     *
     *
     * @param inSourcePassword
     * @param inProvidedPassword
     * @param inData
     * @return
     */
    public boolean isPasswordValid(String inSourcePassword,
                                   String inProvidedPassword,
                                   Object inData);
}
