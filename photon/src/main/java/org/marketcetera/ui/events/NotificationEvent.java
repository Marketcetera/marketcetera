package org.marketcetera.ui.events;

import javafx.scene.control.Alert.AlertType;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class NotificationEvent
{
    /**
     * Create a new NotificationEvent instance.
     *
     * @param inMessage a <code>String</code> value
     */
    public NotificationEvent(String inMessage)
    {
        this(inMessage,
             AlertType.NONE);
    }
    /**
     * Create a new NotificationEvent instance.
     *
     * @param inMessage a <code>String</code> value
     * @param inAlertType an <code>AlertType</code> value
     */
    public NotificationEvent(String inMessage,
                             AlertType inAlertType)
    {
        message = inMessage;
        alertType = inAlertType;
    }
    /**
     * Get the message value.
     *
     * @return a <code>String</code> value
     */
    public String getMessage()
    {
        return message;
    }
    /**
     * Sets the message value.
     *
     * @param inMessage a <code>String</code> value
     */
    public void setMessage(String inMessage)
    {
        message = inMessage;
    }
    /**
     * Get the alertType value.
     *
     * @return an <code>AlertType</code> value
     */
    public AlertType getAlertType()
    {
        return alertType;
    }
    /**
     * Sets the alertType value.
     *
     * @param inAlertType an <code>AlertType</code> value
     */
    public void setAlertType(AlertType inAlertType)
    {
        alertType = inAlertType;
    }
    /**
     * alert type value
     */
    private AlertType alertType;
    /**
     * string message value
     */
    private String message;
}
