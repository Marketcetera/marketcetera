package org.marketcetera.admin.service;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Provides password services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class PasswordServiceImpl
        implements PasswordService
{
    /* (non-Javadoc)
     * @see org.marketcetera.admin.service.PasswordService#getHash(java.lang.String)
     */
    @Override
    public String getHash(String inPassword)
    {
        try {
            MessageDigest dig = digest.get();
            dig.update(inPassword.getBytes("UTF-16")); //$NON-NLS-1$
            return new BigInteger(dig.digest()).toString(Character.MAX_RADIX);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.service.PasswordService#getHash(char[][])
     */
    @Override
    public String getHash(char[]... inValues)
    {
        try {
            MessageDigest dig = digest.get();
            for(char[] c:inValues) {
                dig.update(new String(c).getBytes("UTF-16")); //$NON-NLS-1$
            }
            return new BigInteger(dig.digest()).toString(Character.MAX_RADIX);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }
    /**
     * The digest used to hash the password.
     */
    private static final ThreadLocal<MessageDigest> digest = new ThreadLocal<MessageDigest>()
    {
        /**
         * Get the initial value.
         * 
         * @return a <code>MessageDigest</code> value
         */
        protected MessageDigest initialValue()
        {
            try {
                return MessageDigest.getInstance("SHA1"); //$NON-NLS-1$
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalArgumentException(e);
            }
        }
    };
}
