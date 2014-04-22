package org.marketcetera.util.rpc;

import java.util.Locale;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides credentials to authenticate with an {@link RpcServer}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class RpcCredentials
{
    /**
     * Create a new Credentials instance.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @param inAppId a <code>String</code> value
     * @param inClientId a <code>String</code> value
     * @param inVersionId a <code>String</code> value
     * @param inLocale a <code>Locale</code> value
     */
    public RpcCredentials(String inUsername,
                          String inPassword,
                          String inAppId,
                          String inClientId,
                          String inVersionId,
                          Locale inLocale)
    {
        username = inUsername;
        password = inPassword;
        appId = inAppId;
        clientId = inClientId;
        versionId = inVersionId;
        locale = inLocale;
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
     * Get the password value.
     *
     * @return a <code>String</code> value
     */
    public String getPassword()
    {
        return password;
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
     * Get the appId value.
     *
     * @return a <code>String</code> value
     */
    public String getAppId()
    {
        return appId;
    }
    /**
     * Sets the appId value.
     *
     * @param inAppId a <code>String</code> value
     */
    public void setAppId(String inAppId)
    {
        appId = inAppId;
    }
    /**
     * Get the clientId value.
     *
     * @return a <code>String</code> value
     */
    public String getClientId()
    {
        return clientId;
    }
    /**
     * Sets the clientId value.
     *
     * @param inClientId a <code>String</code> value
     */
    public void setClientId(String inClientId)
    {
        clientId = inClientId;
    }
    /**
     * Get the versionId value.
     *
     * @return a <code>String</code> value
     */
    public String getVersionId()
    {
        return versionId;
    }
    /**
     * Sets the versionId value.
     *
     * @param inVersionId a <code>String</code> value
     */
    public void setVersionId(String inVersionId)
    {
        versionId = inVersionId;
    }
    /**
     * Get the locale value.
     *
     * @return a <code>Locale</code> value
     */
    public Locale getLocale()
    {
        return locale;
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
     * username value
     */
    private String username;
    /**
     * password value
     */
    private String password;
    /**
     * app ID value
     */
    private String appId;
    /**
     * client ID value
     */
    private String clientId;
    /**
     * version ID value
     */
    private String versionId;
    /**
     * locale value
     */
    private Locale locale;
}
