package org.marketcetera.core.notifications;

import java.util.Set;

import javax.mail.Authenticator;

/* $License$ */

/**
 * Provides an email notification implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface EmailNotification
        extends INotification
{
    /**
     * Get the email server authenticator value.
     *
     * @return an <code>Authenticator</code> value
     */
    Authenticator getAuthenticator();
    /**
     * Get the configuration file path.
     *
     * @return a <code>String</code> value
     */
    String getConfigurationFileName();
    /**
     * Get the recipients for this email.
     *
     * @return a <code>Set&t;String&gt;</code> value
     */
    Set<String> getRecipients();
    /**
     * Indicate if this notification should be sent or not.
     *
     * @return a <code>boolean</code> value
     */
    boolean shouldEmail();
}
