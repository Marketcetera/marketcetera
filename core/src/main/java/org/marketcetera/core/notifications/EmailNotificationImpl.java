package org.marketcetera.core.notifications;

import java.util.HashSet;
import java.util.Set;

import javax.mail.Authenticator;

/* $License$ */

/**
 * Provides a custom {@link INotification} for {@link EmailNotificationExecutorMethod}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class EmailNotificationImpl
        extends Notification
        implements EmailNotification
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.EmailNotification#getAuthenticator()
     */
    @Override
    public Authenticator getAuthenticator()
    {
        return authenticator;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.EmailNotification#getConfigurationFileName()
     */
    @Override
    public String getConfigurationFileName()
    {
        return configurationFileName;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.EmailNotification#getRecipients()
     */
    @Override
    public Set<String> getRecipients()
    {
        return recipients;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.EmailNotification#shouldEmail()
     */
    @Override
    public boolean shouldEmail()
    {
        return shouldEmail;
    }
    /**
     * Sets the authenticator value.
     *
     * @param inAuthenticator an <code>Authenticator</code> value
     */
    public void setAuthenticator(Authenticator inAuthenticator)
    {
        authenticator = inAuthenticator;
    }
    /**
     * Sets the configurationFileName value.
     *
     * @param inConfigurationFileName a <code>String</code> value
     */
    public void setConfigurationFileName(String inConfigurationFileName)
    {
        configurationFileName = inConfigurationFileName;
    }
    /**
     * Sets the recipients value.
     *
     * @param inRecipients a <code>Set<String></code> value
     */
    public void setRecipients(Set<String> inRecipients)
    {
        recipients = inRecipients;
    }
    /**
     * Sets the shouldEmail value.
     *
     * @param inShouldEmail a <code>boolean</code> value
     */
    public void setShouldEmail(boolean inShouldEmail)
    {
        shouldEmail = inShouldEmail;
    }
    /**
     * authenticator value
     */
    private Authenticator authenticator;
    /**
     * configuration file name value
     */
    private String configurationFileName;
    /**
     * recipients value
     */
    private Set<String> recipients = new HashSet<>();
    /**
     * indicates if this notification should be sent or not
     */
    private boolean shouldEmail = true;
    private static final long serialVersionUID = -6439894867418161383L;
}
