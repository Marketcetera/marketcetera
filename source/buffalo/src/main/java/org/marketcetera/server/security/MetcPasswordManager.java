package org.marketcetera.server.security;

import org.marketcetera.systemmodel.User;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.security.authentication.dao.SaltSource;
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
public class MetcPasswordManager
        implements PasswordManager
{
    /* (non-Javadoc)
     * @see org.marketcetera.server.security.PasswordManager#encodePassword(java.lang.String, java.lang.Object)
     */
    @Override
    public String encodePassword(String inPassword,
                                 Object inData)
    {
        User user = null;
        if(inData instanceof User) {
            user = (User)inData;
        }
        if(encoder != null) {
            return encoder.encodePassword(inPassword,
                                          getSalt(user));
        } else {
            SLF4JLoggerProxy.warn(MetcPasswordManager.class,
                                  "No password encoder specified");
            return inPassword;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.security.PasswordManager#isPasswordValid(java.lang.String, java.lang.String, java.lang.Object)
     */
    @Override
    public boolean isPasswordValid(String inSourcePassword,
                                   String inProvidedPassword,
                                   Object inData)
    {
        User user = null;
        if(inData instanceof User) {
            user = (User)inData;
        }
        if(encoder != null) {
            return encoder.isPasswordValid(inSourcePassword,
                                           inProvidedPassword,
                                           getSalt(user));
        } else {
            SLF4JLoggerProxy.warn(MetcPasswordManager.class,
                                  "No password encoder specified");
            return inSourcePassword.equals(inProvidedPassword);
        }
    }
    /**
     * Get the encoder value.
     *
     * @return a <code>PasswordEncoder</code> value
     */
    public PasswordEncoder getPasswordEncoder()
    {
        return encoder;
    }
    /**
     * Sets the encoder value.
     *
     * @param a <code>PasswordEncoder</code> value
     */
    public void setPasswordEncoder(PasswordEncoder inEncoder)
    {
        encoder = inEncoder;
    }
    /**
     * Get the saltSource value.
     *
     * @return a <code>SaltSource</code> value
     */
    public SaltSource getSaltSource()
    {
        return saltSource;
    }
    /**
     * Sets the saltSource value.
     *
     * @param a <code>SaltSource</code> value
     */
    public void setSaltSource(SaltSource inSaltSource)
    {
        saltSource = inSaltSource;
    }
    /**
     * 
     *
     *
     * @return
     */
    private Object getSalt(User inUserImpl)
    {
        return saltSource == null ? null : saltSource.getSalt(new MetcUserDetails(inUserImpl));
    }
    /**
     * 
     */
    private PasswordEncoder encoder;
    /**
     * 
     */
    private SaltSource saltSource;
}
