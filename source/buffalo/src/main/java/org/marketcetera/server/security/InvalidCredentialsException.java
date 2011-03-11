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
public class InvalidCredentialsException
        extends AuthenticationException
{
    /**
     * Create a new InvalidCredentialsException instance.
     *
     * @param inMsg
     */
    public InvalidCredentialsException(String inMsg)
    {
        super(inMsg);
    }
    private static final long serialVersionUID = 1L;
}
