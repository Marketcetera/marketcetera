package org.marketcetera.core;

import java.util.Locale;

/* $License$ */

/**
 * Provides common parameters to connect to client services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class BaseClientParameters
        implements ClientParameters
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.ClientParameters#getHostname()
     */
    @Override
    public String getHostname()
    {
        return hostname;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ClientParameters#getPort()
     */
    @Override
    public int getPort()
    {
        return port;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ClientParameters#getUsername()
     */
    @Override
    public String getUsername()
    {
        return username;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ClientParameters#getPassword()
     */
    @Override
    public String getPassword()
    {
        return password;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ClientParameters#getLocale()
     */
    @Override
    public Locale getLocale()
    {
        return locale;
    }
    /**
     * Sets the hostname value.
     *
     * @param inHostname a <code>String</code> value
     */
    public void setHostname(String inHostname)
    {
        hostname = inHostname;
    }
    /**
     * Sets the port value.
     *
     * @param inPort a <code>int</code> value
     */
    public void setPort(int inPort)
    {
        port = inPort;
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
     * Sets the password value.
     *
     * @param inPassword a <code>String</code> value
     */
    public void setPassword(String inPassword)
    {
        password = inPassword;
    }
    /**
     * Sets the locale value.
     *
     * @param inLocale a <code>Locale</code> value
     */
    public void setLocale(Locale inLocale)
    {
        locale = inLocale;
    }
    /**
     * hostname value
     */
    private String hostname;
    /**
     * port value
     */
    private int port;
    /**
     * username value
     */
    private String username;
    /**
     * password value
     */
    private String password;
    /**
     * locale value
     */
    private Locale locale;
}
