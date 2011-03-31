package org.marketcetera.server.security;

import org.springframework.security.core.AuthenticationException;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class UnsupportedAuthenticationException
        extends AuthenticationException
{
    /**
     * Create a new UnsupportedAuthenticationException instance.
     *
     * @param inMsg
     */
    public UnsupportedAuthenticationException(String inMsg)
    {
        super(inMsg);
    }
    private static final long serialVersionUID = 1L;
}
