package org.marketcetera.marketdata.core.webservice;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
public class CredentialsException
        extends CoreException
{
    /**
     * Create a new ConnectionException instance.
     */
    public CredentialsException()
    {
    }
    /**
     * Create a new CredentialsException instance.
     *
     * @param inUsername a <code>String</code> value
     */
    public CredentialsException(String inUsername)
    {
        username = inUsername;
    }
    /**
     * Create a new ConnectionException instance.
     *
     * @param inNested a <code>Throwable</code> value
     */
    public CredentialsException(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new ConnectionException instance.
     *
     * @param inMessage an <code>I18nBoundMessage</code> value
     */
    public CredentialsException(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new ConnectionException instance.
     *
     * @param inNested a <code>Throwable</code> value
     * @param inMessage an <code>I18nBoundMessage</code> value
     */
    public CredentialsException(Throwable inNested,
                               I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
    /**
     * Get the username value.
     *
     * @return a <code>String</code> value
     */
    public String getUsername()
    {
        return username;
    }
    /**
     * Sets the username value.
     *
     * @param inUsername a <code>String</code> value
     */
    public void setUsername(String inUsername)
    {
        username = inUsername;
    }
    /**
     * 
     */
    private String username;
    private static final long serialVersionUID = 8121822556635716771L;
}
