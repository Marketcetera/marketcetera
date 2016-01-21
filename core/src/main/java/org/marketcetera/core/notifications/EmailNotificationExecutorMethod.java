package org.marketcetera.core.notifications;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/* $License$ */

/**
 * Sends notifications via email.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: EmailNotificationExecutor.java 85060 2015-12-16 03:19:37Z colin $
 * @since $Release$
 */
public class EmailNotificationExecutorMethod
        extends AbstractNotificationExecutorMethod
{
    /**
     * Get the authenticator value.
     *
     * @return an <code>Authenticator</code> value
     */
    public Authenticator getAuthenticator()
    {
        return authenticator;
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
     * Get the configurationFileName value.
     *
     * @return a <code>String</code> value
     */
    public String getConfigurationFileName()
    {
        return configurationFileName;
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
     * Get the notificationRecipients value.
     *
     * @return a <code>Multimap&lt;INotification.Severity,String&gt;</code> value
     */
    public Multimap<INotification.Severity,String> getNotificationRecipients()
    {
        return notificationRecipients;
    }
    /**
     * Sets the notificationRecipients value.
     *
     * @param a <code>Map&lt;INotification.Severity,String&gt;</code> value
     */
    public void setNotificationRecipients(Map<INotification.Severity,String> inNotificationRecipients)
    {
        notificationRecipients.clear();
        if(inNotificationRecipients == null) {
            return;
        }
        for(Map.Entry<INotification.Severity,String> entry : inNotificationRecipients.entrySet()) {
            String inRecipients = entry.getValue();
            inRecipients = StringUtils.trimToNull(inRecipients);
            if(inRecipients != null) {
                for(String recipient : inRecipients.split(",")) {
                    recipient = StringUtils.trimToNull(recipient);
                    if(recipient != null) {
                        notificationRecipients.put(entry.getKey(),
                                                   recipient);
                    }
                }
            }
            
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.AbstractNotificationExecutorMethod#doNotify(org.marketcetera.core.notifications.INotification)
     */
    @Override
    protected void doNotify(INotification inNotification)
            throws Exception
    {
        sendEmail(inNotification);
    }
    /**
     * Sends a notification email, if necessary.
     *
     * @param inNotification an <code>INotification</code> value
     * @throws Exception if the email could not be sent
     */
    private void sendEmail(INotification inNotification)
            throws Exception
    {
        Collection<String> recipients = notificationRecipients.get(inNotification.getSeverity());
        if(recipients == null || recipients.isEmpty()) {
            SLF4JLoggerProxy.warn(this,
                                  "Not sending email notification because there are no recipients defined: {}",
                                  inNotification);
        } else {
            SLF4JLoggerProxy.debug(this,
                                   "Sending email notification for: {}",
                                   inNotification);
            Properties mailServerConfig;
            try {
                mailServerConfig = readConfig();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Session session = Session.getDefaultInstance(mailServerConfig,
                                                         authenticator);
            MimeMessage message = new MimeMessage(session);
            InternetAddress[] addresses = new InternetAddress[recipients.size()];
            int counter = 0;
            for(String recipient : recipients) {
                addresses[counter++] = new InternetAddress(recipient);
            }
            message.addRecipients(Message.RecipientType.TO,
                                  addresses);
            message.setSubject(getSubject(inNotification));
            message.setText(getBody(inNotification));
            Transport.send(message);
        }
    }
    /**
     * Reads the email sending configuration.
     *
     * @return a <code>Properties</code> value
     * @throws IOException if the configuration cannot be read
     */
    private Properties readConfig()
            throws IOException
    {
        InputStream input = null;
        try {
            input = new FileInputStream(configurationFileName);
            Properties config = new Properties();
            config.load(input);
            return config;
        } finally {
            if(input != null) {
                input.close();
            }
        }
    }
    /**
     * email recipients by severity
     */
    private final Multimap<INotification.Severity,String> notificationRecipients = HashMultimap.create();
    /**
     * authenticator to use or <code>null</code>
     */
    private Authenticator authenticator;
    /**
     * contains the name of the mail server configuration file
     */
    private String configurationFileName = "conf" + File.separator + "mail.properties"; //$NON-NLS-1$ //$NON-NLS-2$
}
