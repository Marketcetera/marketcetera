package org.marketcetera.server.security;

import org.marketcetera.server.service.UserManager;
import org.marketcetera.systemmodel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.CredentialsContainer;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MetcAuthenticationProvider
        implements AuthenticationProvider
{
    /* (non-Javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
     */
    @Override
    public Authentication authenticate(Authentication inAuthentication)
            throws AuthenticationException
    {
        try {
            if(!supports(inAuthentication.getClass())) {
                throw new UnsupportedAuthenticationException("Unexpected authentication type: " + inAuthentication.getClass());
            }
            User user = userManager.getByName(inAuthentication.getName());
            if(user == null) {
                throw new InvalidCredentialsException("Unknown user or incorrect password");
            }
            inAuthentication.setAuthenticated(passwordManager.isPasswordValid(user.getHashedPassword(),
                                                                              (String)inAuthentication.getCredentials(),
                                                                              user));
            return inAuthentication;
        } finally {
            if(inAuthentication instanceof CredentialsContainer) {
                ((CredentialsContainer)inAuthentication).eraseCredentials();
            }
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class<? extends Object> inClass)
    {
        return inClass.isAssignableFrom(MetcAuthentication.class);
    }
    /**
     * 
     */
    @Autowired
    private UserManager userManager;
    /**
     * 
     */
    @Autowired
    private PasswordManager passwordManager;
}
