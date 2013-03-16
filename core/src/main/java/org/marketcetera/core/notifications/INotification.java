package org.marketcetera.core.notifications;

import java.io.Serializable;
import java.util.Date;

/* $License$ */

/**
 * Represents an event that has occurred.
 *
 * @version $Id: INotification.java 16063 2012-01-31 18:21:55Z colin $
 * @since 0.8.0
 */
public interface INotification
    extends Serializable
{
    /**
     * Indicates the severity of an <code>INotification</code>.
     *
     * @version $Id: INotification.java 16063 2012-01-31 18:21:55Z colin $
     * @since 0.8.0
     */
        public enum Severity
    {
        LOW,
        MEDIUM,
        HIGH
    }
    /**
     * Gets the high-level summary of the <code>INotification</code>.
     *
     * @return a <code>String</code> value
     */
    public String getSubject();
    /**
     * Gets the complete text of the <code>INotification</code>.
     *
     * @return a <code>String</code> value
     */
    public String getBody();
    /**
     * Gets the <code>Severity</code> of the <code>INotification</code>.
     *
     * @return a <code>Severity</code> value
     */
    public Severity getSeverity();
    /**
     * Gets the time that the <code>INotification</code> occurred.
     *
     * @return a <code>Date</code> value
     */
    public Date getDate();
    /**
     * Gets a description of the originator of the <code>INotification</code>.
     *
     * @return a <code>String</code> value
     */
    public String getOriginator();
}
