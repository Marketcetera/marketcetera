package org.marketcetera.server.security;

import org.marketcetera.systemmodel.User;




/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface PasswordManager
{
    public String encodePassword(User inUser,
                                 String inPassword);
}
