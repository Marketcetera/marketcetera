package org.marketcetera.server.security;

import org.marketcetera.systemmodel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class PasswordManagerImpl
        implements PasswordManager
{
    /* (non-Javadoc)
     * @see org.marketcetera.server.security.PasswordManager#encodePassword(org.marketcetera.systemmodel.User, java.lang.String)
     */
    @Override
    public String encodePassword(User inUser,
                                 String inPassword)
    {
        return encoder.encodePassword(inPassword,
                                      null);
    }
    /**
     * password encoder used to encode passwords
     */
    @Autowired
    private PasswordEncoder encoder;
    /**
     * 
     */
//    @Autowired
//    private SaltSource saltSource;
}
